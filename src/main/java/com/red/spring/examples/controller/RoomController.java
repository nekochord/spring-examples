package com.red.spring.examples.controller;

import com.red.spring.examples.entity.Room;
import com.red.spring.examples.entity.RoomMessage;
import com.red.spring.examples.repository.RoomMessageRepository;
import com.red.spring.examples.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private RoomMessageRepository roomMessageRepository;

    @GetMapping("/all")
    public Flux<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @GetMapping("/{roomId}/messages")
    public Flux<RoomMessage> getRoomMessages(@PathVariable String roomId) {
        return roomMessageRepository.findByRoomIdOrderByTimeAsc(roomId);
    }
}
