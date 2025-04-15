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
public class WorkQueues {
    @Autowired
    private RabbitAdmin rabbitAdmin;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Bean
    public Queue workersQueue() {
        return new Queue("workers", false);
    }

    @Scheduled(fixedDelayString = "5s")
    public void send() {
        long currentTimeMillis = System.currentTimeMillis();
        rabbitTemplate.convertAndSend("", "workers", currentTimeMillis);
        log.info("Sent message: {}", currentTimeMillis);
    }

    @RabbitListener(queues = "workers")
    public void worker_1(String message) {
        log.info("worker_1 received message: {}", message);
    }

    @RabbitListener(queues = "workers")
    public void worker_2(String message) {
        log.info("worker_2 received message: {}", message);
    }
}
