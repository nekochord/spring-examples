package com.red.spring.examples.rabbitmq;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
public class Routing {
    @Autowired
    private RabbitAdmin rabbitAdmin;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        Queue routeAQueue = new Queue("routeA", false);
        rabbitAdmin.declareQueue(routeAQueue);
        Exchange routingExchange = new DirectExchange("routing-direct-exchange");
        rabbitAdmin.declareExchange(routingExchange);
        rabbitAdmin.declareBinding(
                BindingBuilder.bind(routeAQueue)
                        .to(routingExchange)
                        .with("A")
                        .noargs()
        );
    }

    @Scheduled(fixedDelayString = "5s")
    public void send() {
        rabbitTemplate.convertAndSend("routing-direct-exchange", "A", "route AAA");
        log.info("Sent message: route AAA");
    }

    @Scheduled(fixedDelayString = "5s")
    public void sendIgnore() {
        rabbitTemplate.convertAndSend("routing-direct-exchange", "NO", "route NO");
        log.info("Sent message: route NO");
    }

    @RabbitListener(queues = "routeA")
    public void receiveA(String message) {
        log.info("routeA received message: {}", message);
    }

}
