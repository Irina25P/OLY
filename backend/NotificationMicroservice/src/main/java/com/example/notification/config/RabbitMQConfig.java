package com.example.notification.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
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

    @Value("${rabbitmq.sync.exchange.user.topic}")
    private String userTopicExchange;

    /**
     * notifications routing keys
     */

    @Value("${rabbitmq.sync.routingkey.orderCreated}")
    private String orderCreatedRoutingKey;

    @Value("${rabbitmq.sync.routingkey.orderUpdated}")
    private String orderUpdatedRoutingKey;

    @Value("${rabbitmq.sync.routingkey.reviewCreated}")
    private String reviewCreatedRoutingKey;

    /**
     * user sync routing keys
     */

    @Value("${rabbitmq.sync.routingkey.client}")
    private String addClientRoutingKey;

    @Value("${rabbitmq.sync.routingkey.seller}")
    private String addSellerRoutingKey;

    @Value("${rabbitmq.sync.routingkey.deleteUser}")
    private String deleteUserRoutingKey;

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
    public TopicExchange userTopicExchange() {
        return new TopicExchange(userTopicExchange, true, false);
    }

    @Bean
    public Queue addClientQueue() {
        return new Queue(userTopicExchange + "." + uniqueQueueName + "." + addClientRoutingKey, true, false, false);
    }

    @Bean
    public Binding addClientBinding() {
        return BindingBuilder.bind(addClientQueue())
                .to(userTopicExchange())
                .with(addClientRoutingKey);
    }

    @Bean
    public Queue addSellerQueue() {
        return new Queue(userTopicExchange + "." + uniqueQueueName + "." + addSellerRoutingKey, true, false, false);
    }

    @Bean
    public Binding addSellerBinding() {
        System.out.println(userTopicExchange + "." + uniqueQueueName + "." + addSellerRoutingKey);
        return BindingBuilder.bind(addSellerQueue())
                .to(userTopicExchange())
                .with(addSellerRoutingKey);
    }

    @Bean
    public Queue deleteUserQueue() {
        return new Queue(userTopicExchange + "." + uniqueQueueName + "." + deleteUserRoutingKey, true, false, false);
    }

    @Bean
    public Binding deleteUserBinding() {
        return BindingBuilder.bind(deleteUserQueue())
                .to(userTopicExchange())
                .with(deleteUserRoutingKey);
    }
}
