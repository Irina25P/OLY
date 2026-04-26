package com.example.imagemanagementmicroservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.sync.exchange.deleteImages.topic}")
    private String deleteImagesExchangeName;

    @Value("${rabbitmq.sync.exchange.deleteImages.routingkey}")
    private String deleteImagesRoutingKey;

    @Value("${rabbitmq.sync.queue.unique.name}")
    private String uniqueQueueName;

    @Bean
    public TopicExchange deleteImagesTopicExchange() {
        return new TopicExchange(deleteImagesExchangeName, true, false);
    }

    @Bean
    public Queue deleteImagesQueue() {
        return new Queue(deleteImagesExchangeName + "." + uniqueQueueName, true, false, false);
    }

    @Bean
    public Binding deleteImagesBinding(Queue deleteImagesQueue, TopicExchange deleteImagesTopicExchange) {
        return BindingBuilder.bind(deleteImagesQueue)
                .to(deleteImagesTopicExchange)
                .with(deleteImagesRoutingKey);
    }
}

