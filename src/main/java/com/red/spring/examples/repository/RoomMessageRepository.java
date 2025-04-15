package com.red.spring.examples.repository;

import com.red.spring.examples.entity.RoomMessage;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface RoomMessageRepository extends ReactiveMongoRepository<RoomMessage, String> {

    Flux<RoomMessage> findByRoomIdOrderByTimeAsc(String roomId);

}
