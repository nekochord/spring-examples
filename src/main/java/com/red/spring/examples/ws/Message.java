package com.red.spring.examples.ws;

import lombok.Data;

@Data
public class Message {
    private Type type;
    private String roomId;
    private String message;

    public enum Type {
        SUBSCRIBE,
        UNSUBSCRIBE,
        MESSAGE,
        ;
    }
}
