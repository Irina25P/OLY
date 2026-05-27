package com.example.order.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Value("${rabbitmq.sync.queue.unique.name}")
    private String uniqueQueueName;

    /**
     * user queue topic and routing keys for consuming messages
     */
    @Value("${rabbitmq.sync.exchange.user.topic}")
    private String userTopicExchange;

    @Value("${rabbitmq.sync.routingkey.client}")
    private String addClientRoutingKey;

    @Value("${rabbitmq.sync.routingkey.seller}")
    private String addSellerRoutingKey;

    @Value("${rabbitmq.sync.routingkey.deleteUser}")
    private String deleteUserRoutingKey;

    /**
     * product queue topic and routing keys for consuming messages
     */
    @Value("${rabbitmq.sync.exchange.product.topic}")
    private String productTopicExchange;

    @Value("${rabbitmq.sync.routingkey.addProduct}")
    private String addProductRoutingKey;

    @Value("${rabbitmq.sync.routingkey.updateProduct}")
    private String updateProductRoutingKey;

    @Value("${rabbitmq.sync.routingkey.deleteProduct}")
    private String deleteProductRoutingKey;

    /**
     * order event topic for recommendation and pricing services
     */
    @Value("${rabbitmq.sync.exchange.order.event.topic}")
    private String orderEventTopicExchange;

    @Value("${rabbitmq.sync.routingkey.orderEvent}")
    private String orderEventRoutingKey;

    /**
     * notification topic
     */

    @Value("${rabbitmq.sync.exchange.notification.topic}")
    private String notificationTopicExchange;

    @Bean
    public TopicExchange notificationTopicExchange() {
        return new TopicExchange(notificationTopicExchange, true, false);
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

    @Bean
    public TopicExchange productTopicExchange() {
        return new TopicExchange(productTopicExchange, true, false);
    }

    @Bean
    public Queue addProductQueue() {
        return new Queue(productTopicExchange + "." + uniqueQueueName + "." + addProductRoutingKey, true, false, false);
    }

    @Bean
    public Binding addProductBinding() {
        return BindingBuilder.bind(addProductQueue())
                .to(productTopicExchange())
                .with(addProductRoutingKey);
    }

    @Bean
    public Queue updateProductQueue() {
        return new Queue(productTopicExchange + "." + uniqueQueueName + "." + updateProductRoutingKey, true, false, false);
    }

    @Bean
    public Binding updateProductBinding() {
        return BindingBuilder.bind(updateProductQueue())
                .to(productTopicExchange())
                .with(updateProductRoutingKey);
    }

    @Bean
    public Queue deleteProductQueue() {
        return new Queue(productTopicExchange + "." + uniqueQueueName + "." + deleteProductRoutingKey, true, false, false);
    }

    @Bean
    public Binding deleteProductBinding() {
        return BindingBuilder.bind(deleteProductQueue())
                .to(productTopicExchange())
                .with(deleteProductRoutingKey);
    }

    @Bean
    public TopicExchange orderEventTopicExchange() {
        return new TopicExchange(orderEventTopicExchange, true, false);
    }
}
