package com.example.product.configs;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.sync.exchange.product.topic}")
    private String productExchange;

    @Value("${rabbitmq.sync.exchange.deleteImage.topic}")
    private String deleteImageExchange;

    @Value("${rabbitmq.sync.exchange.changeStock.topic}")
    private String changeStockExchange;

    @Value("${rabbitmq.sync.exchange.notification.topic}")
    private String notificationExchange;

    @Value("${rabbitmq.sync.routingKey.changeStock}")
    private String changeStockRoutingKey;

    @Value("${rabbitmq.sync.exchange.user.topic}")
    private String userExchange;

    @Value("${rabbitmq.sync.routingkey.client}")
    private String clientRoutingKey;

    @Value("${rabbitmq.sync.routingkey.seller}")
    private String sellerRoutingKey;

    @Value("${rabbitmq.sync.routingkey.deleteUser}")
    private String deleteUserRoutingKey;

    @Value("${rabbitmq.sync.queue.unique.name}")
    private String uniqueQueueName;

    @Bean
    public TopicExchange userTopicExchange() {
        return new TopicExchange(userExchange, true, false);
    }

    @Bean
    public TopicExchange productTopicExchange() {
        return new TopicExchange(productExchange, true, false);
    }

    @Bean
    public TopicExchange deleteImageTopicExchange() {
        return new TopicExchange(deleteImageExchange, true, false);
    }

    @Bean
    public TopicExchange changeStockTopicExchange() {
        return new TopicExchange(changeStockExchange, true, false);
    }

    @Bean
    public TopicExchange notificationTopicExchange() {
        return new TopicExchange(notificationExchange, true, false);
    }

    @Bean
    public Queue changeStockQueue() {
        return new Queue(changeStockExchange + "." + uniqueQueueName + "." + changeStockRoutingKey,true, false, false);
    }

    @Bean
    public Queue addClientQueue() {
        return new Queue(userExchange + "." + uniqueQueueName + "." + clientRoutingKey, true, false, false);
    }

    @Bean
    public Queue addSellerQueue() {
        return new Queue(userExchange + "." + uniqueQueueName + "." + sellerRoutingKey, true, false, false);
    }

    @Bean
    public Queue deleteUserQueue() {
        return new Queue(userExchange + "." + uniqueQueueName + "." + deleteUserRoutingKey, true, false, false);
    }

    @Bean
    public Binding changeStockBinding() {
        return BindingBuilder.bind(changeStockQueue()).
                to(changeStockTopicExchange()).
                with(changeStockRoutingKey);
    }

    @Bean
    public Binding addClientBinding() {
        return BindingBuilder
                .bind(addClientQueue())
                .to(userTopicExchange())
                .with(clientRoutingKey);
    }

    @Bean
    public Binding addSellerBinding() {
        return BindingBuilder
                .bind(addSellerQueue())
                .to(userTopicExchange())
                .with(sellerRoutingKey);
    }

    @Bean
    public Binding deleteUserBinding() {
        return BindingBuilder
                .bind(deleteUserQueue())
                .to(userTopicExchange())
                .with(deleteUserRoutingKey);
    }
}