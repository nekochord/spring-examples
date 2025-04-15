package com.red.spring.examples.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("rooms")
public class Room {
    @Id
    private String id;
    private String name;
}
