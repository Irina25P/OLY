package com.example.notification.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

@Configuration
@EnableRabbit
public class RabbitMQConfig {
    @Value("${rabbitmq.sync.queue.unique.name}")
    private String uniqueQueueName;

    @Value("${rabbitmq.sync.exhange.notification.topic}")
    private String notificationTopicExchange;

    @Value("${rabbitmq.sync.routingkey.orderCreated}")
    private String orderCreatedRoutingKey;

    @Value("${rabbitmq.sync.routingkey.orderUpdated}")
    private String orderUpdatedRoutingKey;

    @Value("${rabbitmq.sync.routingkey.reviewCreated}")
    private String reviewCreatedRoutingKey;

    @Bean
    public TopicExchange notificationTopicExchange() {
        return new TopicExchange(notificationTopicExchange, true, false);
    }

    @Bean
    public Queue orderCreatedQueue() {
        return new Queue( notificationTopicExchange + "." + uniqueQueueName + "." + orderCreatedRoutingKey, true, false, false);
    }

    @Bean
    public Binding orderCreatedBinding() {
        return BindingBuilder.bind(orderCreatedQueue())
                .to(notificationTopicExchange())
                .with(orderCreatedRoutingKey);
    }

    @Bean
    public Queue orderUpdatedQueue() {
        return new Queue( notificationTopicExchange + "." + uniqueQueueName + "." + orderUpdatedRoutingKey, true, false, false);
    }

    @Bean
    public Binding orderUpdatedBinding() {
        return BindingBuilder.bind(orderUpdatedQueue())
                .to(notificationTopicExchange())
                .with(orderUpdatedRoutingKey);
    }

    @Bean
    public Queue reviewCreatedQueue() {
        return new Queue( notificationTopicExchange + "." + uniqueQueueName + "." + reviewCreatedRoutingKey, true, false, false);
    }

    @Bean
    public Binding reviewCreatedBinding() {
        return BindingBuilder.bind(reviewCreatedQueue())
                .to(notificationTopicExchange())
                .with(reviewCreatedRoutingKey);
    }

    @Bean
    public JacksonJsonMessageConverter jackson2JsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter jackson2JsonMessageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter);
        return factory;
    }
}
