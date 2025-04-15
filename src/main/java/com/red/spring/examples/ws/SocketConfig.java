package com.red.spring.examples.ws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Configuration
public class SocketConfig {

    @Bean
    public HandlerMapping handlerMapping(SocketMessageHandler handler) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/channel", handler);
        return new SimpleUrlHandlerMapping(map, 1);
    }

    @Bean
    public RouterFunction<ServerResponse> htmlRouter(
            @Value("classpath:/static/chat.html") Resource html
    ) {
        return route(GET("/chat.html"), request
                -> ok().contentType(MediaType.TEXT_HTML).bodyValue(html)
        );
    }

}
