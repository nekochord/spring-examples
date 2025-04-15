package com.red.spring.examples.rabbitmq;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
public class Topic {
    @Autowired
    private RabbitAdmin rabbitAdmin;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        Queue topicAQueue = new Queue("topicA", false);
        Queue topicBQueue = new Queue("topicB", false);
        rabbitAdmin.declareQueue(topicAQueue);
        rabbitAdmin.declareQueue(topicBQueue);
        Exchange topicExchange = new TopicExchange("topic-exchange");
        rabbitAdmin.declareExchange(topicExchange);
        rabbitAdmin.declareBinding(
                BindingBuilder.bind(topicAQueue)
                        .to(topicExchange)
                        .with("A.#")
                        .noargs()
        );
        rabbitAdmin.declareBinding(
                BindingBuilder.bind(topicBQueue)
                        .to(topicExchange)
                        .with("B.#")
                        .noargs()
        );
    }

    @Scheduled(fixedDelayString = "5s")
    public void send() {
        rabbitTemplate.convertAndSend("topic-exchange", "A.123", "AAA");
        rabbitTemplate.convertAndSend("topic-exchange", "B.123", "BBB");
        log.info("Sent message");
    }

}
