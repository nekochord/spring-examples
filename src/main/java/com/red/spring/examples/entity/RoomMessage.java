package com.red.spring.examples.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document("messages")
public class RoomMessage {
    @Id
    private String id;
    private String roomId;
    private String content;
    private Instant time;
}
