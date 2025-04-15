package com.red.spring.examples.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
public class HelloWorld {
    @Autowired
    private RabbitAdmin rabbitAdmin;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Bean
    public Queue helloWorldQueue() {
        return new Queue("hello-world", false);
    }

    @Scheduled(fixedDelayString = "5s")
    public void send() {
        rabbitTemplate.convertAndSend("", "hello-world", "Hello, World!");
        log.info("Sent message: Hello, World!");
    }

    @RabbitListener(queues = "hello-world")
    public void receive(String message) {
        log.info("Received message: {}", message);
    }
}
