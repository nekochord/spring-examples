package com.red.spring.examples.rabbitmq;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
public class PublishSubscribe {
    @Autowired
    private RabbitAdmin rabbitAdmin;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        Queue subscriber1Queue = new Queue("subscriber1", false);
        Queue subscriber2Queue = new Queue("subscriber2", false);
        rabbitAdmin.declareQueue(subscriber1Queue);
        rabbitAdmin.declareQueue(subscriber2Queue);
        Exchange fanoutExchange = new FanoutExchange("publisher-fanout-exchange");
        rabbitAdmin.declareExchange(fanoutExchange);
        rabbitAdmin.declareBinding(
                BindingBuilder.bind(subscriber1Queue)
                        .to(fanoutExchange)
                        .with("")
                        .noargs()
        );
        rabbitAdmin.declareBinding(
                BindingBuilder.bind(subscriber2Queue)
                        .to(fanoutExchange)
                        .with("")
                        .noargs()
        );
    }

    @Scheduled(fixedDelayString = "5s")
    public void send() {
        String message = String.valueOf(System.currentTimeMillis());
        rabbitTemplate.convertAndSend("publisher-fanout-exchange", "", message);
        log.info("Sent message: {}", message);
    }

    @RabbitListener(queues = "subscriber1")
    public void receive1(String message) {
        log.info("subscriber1 received message: {}", message);
    }

    @RabbitListener(queues = "subscriber2")
    public void receive2(String message) {
        log.info("subscriber2 received message: {}", message);
    }
}
