package com.red.spring.examples.controller;

import com.red.spring.examples.entity.Room;
import com.red.spring.examples.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomRepository roomRepository;

    @GetMapping("/all")
    public Flux<Room> getAllRooms() {
        return roomRepository.findAll();
    }
}
