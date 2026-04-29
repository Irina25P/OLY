package com.example.authmicroservice.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.sync.exchange.user.topic}")
    private String userTopicExchange;

    @Value("${rabbitmq.sync.queue.unique.name}")
    private String uniqueQueueName;

    @Value("${rabbitmq.sync.routingkey.deleteUser}")
    private String deleteUserRoutingKey;

    @Bean
    public TopicExchange userTopicExchange() {
        return new TopicExchange(userTopicExchange, true, false);
    }

    @Bean
    public Queue deleteUserQueue() {
        return new Queue(userTopicExchange + "." + uniqueQueueName + "." + deleteUserRoutingKey, true, false, false);
    }

    @Bean
    public Binding deleteUserBinding() {
        return BindingBuilder.bind(deleteUserQueue()).to(userTopicExchange()).with(deleteUserRoutingKey);
    }
}


