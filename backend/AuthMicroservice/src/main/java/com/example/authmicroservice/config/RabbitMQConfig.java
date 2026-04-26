package com.example.authmicroservice.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.sync.exchange.addUser.topic}")
    private String addUserTopicExchange;

    @Value("${rabbitmq.sync.exchange.deleteUser.topic}")
    private String deleteUserTopicExchange;

    @Value("${rabbitmq.sync.queue.unique.name}")
    private String uniqueQueueName;

    @Value("${rabbitmq.sync.routingkey.deleteUser}")
    private String deleteUserRoutingKey;

    @Bean
    public TopicExchange addUserTopicExchange() {
        return new TopicExchange(addUserTopicExchange, true, false);
    }

    @Bean
    public TopicExchange deleteUserTopicExchange() {
        return new TopicExchange(deleteUserTopicExchange, true, false);
    }

    @Bean
    public Queue deleteUserQueue() {
        return new Queue(deleteUserTopicExchange + "." + uniqueQueueName + "." + deleteUserRoutingKey, true, false, false);
    }

    @Bean
    public Binding deleteUserBinding() {
        return BindingBuilder.bind(deleteUserQueue()).to(deleteUserTopicExchange()).with(deleteUserRoutingKey);
    }
}


