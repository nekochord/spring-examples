package com.red.spring.examples.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.model.changestream.OperationType;
import com.red.spring.examples.entity.RoomMessage;
import com.red.spring.examples.repository.RoomMessageRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;

@Slf4j
@Component
public class SocketMessageHandler implements WebSocketHandler {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final ConcurrentHashMap<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, KeySetView<String, Boolean>> roomMap = new ConcurrentHashMap<>();
    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;
    @Autowired
    private RoomMessageRepository roomMessageRepository;
    @Autowired
    private DataBufferFactory dataBufferFactory;

    private static Optional<Message> convert(WebSocketMessage webSocketMessage) {
        String text = webSocketMessage.getPayloadAsText();
        try {
            return Optional.of(MAPPER.readValue(text, Message.class));
        } catch (Exception e) {
            log.error("illegal message, message={}", text);
            return Optional.empty();
        }
    }

    @PostConstruct
    public void init() {
        reactiveMongoTemplate.changeStream(RoomMessage.class)
                .watchCollection("messages")
                .listen()
                .doOnNext(event -> {
                    log.info("receive event: {}", event);
                    OperationType operationType = event.getOperationType();
                    RoomMessage roomMessage = event.getBody();
                    if (OperationType.INSERT == operationType && Objects.nonNull(roomMessage)) {
                        KeySetView<String, Boolean> sessionIdSet = roomMap.get(roomMessage.getRoomId());
                        if (Objects.nonNull(sessionIdSet)) {
                            Message message = new Message();
                            message.setType(Message.Type.MESSAGE);
                            message.setRoomId(roomMessage.getRoomId());
                            message.setMessage(roomMessage.getContent());

                            WebSocketMessage webSocketMessage;
                            try {
                                webSocketMessage = new WebSocketMessage(
                                        WebSocketMessage.Type.TEXT,
                                        dataBufferFactory.wrap(MAPPER.writeValueAsBytes(message))
                                );
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                            for (String sessionId : sessionIdSet) {
                                WebSocketSession session = sessionMap.get(sessionId);
                                if (Objects.nonNull(session)) {
                                    session.send(Mono.justOrEmpty(webSocketMessage)).subscribe();
                                }
                            }
                        }
                    }
                }).subscribe();
    }

    private void subscribe(String roomId, String sessionId) {
        roomMap.putIfAbsent(roomId, ConcurrentHashMap.newKeySet());
        roomMap.get(roomId).add(sessionId);
    }

    private void unsubscribe(String roomId, String sessionId) {
        roomMap.putIfAbsent(roomId, ConcurrentHashMap.newKeySet());
        roomMap.get(roomId).remove(sessionId);
    }

    private void send(Message message) {
        RoomMessage roomMessage = new RoomMessage();
        roomMessage.setRoomId(message.getRoomId());
        roomMessage.setContent(message.getMessage());
        roomMessage.setTime(Instant.now());
        roomMessageRepository.save(roomMessage).subscribe();
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        final String sessionId = session.getId();
        sessionMap.put(sessionId, session);
        return session.receive().doOnNext(webSocketMessage -> {
            Optional<Message> messageOpt = convert(webSocketMessage);
            if (messageOpt.isPresent()) {
                Message message = messageOpt.get();
                log.info("receive message: {}", message);
                String roomId = message.getRoomId();
                switch (message.getType()) {
                    case SUBSCRIBE -> subscribe(roomId, sessionId);
                    case UNSUBSCRIBE -> unsubscribe(roomId, sessionId);
                    case MESSAGE -> send(message);
                }
            }
        }).doOnTerminate(() -> {
            log.info("remove session: {}", sessionId);
            sessionMap.remove(sessionId);
        }).then();
    }
}
