package com.example.dynamicpricing.configs;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.sync.exchange.order.event.topic}")
    private String orderEventExchange;

    @Value("${rabbitmq.sync.routingkey.orderEvent}")
    private String orderEventRoutingKey;

    @Value("${rabbitmq.sync.exchange.product.topic}")
    private String productExchange;

    @Value("${rabbitmq.sync.routingkey.addProduct}")
    private String addProductRoutingKey;

    @Value("${rabbitmq.sync.routingkey.updateProduct}")
    private String updateProductRoutingKey;

    @Value("${rabbitmq.sync.routingkey.deleteProduct}")
    private String deleteProductRoutingKey;

    @Value("${rabbitmq.sync.exchange.price.update.topic}")
    private String priceUpdateExchange;

    @Value("${rabbitmq.sync.queue.unique.name}")
    private String uniqueQueueName;

    @Bean
    public TopicExchange orderEventTopicExchange() {
        return new TopicExchange(orderEventExchange, true, false);
    }

    @Bean
    public TopicExchange productTopicExchange() {
        return new TopicExchange(productExchange, true, false);
    }

    @Bean
    public TopicExchange priceUpdateTopicExchange() {
        return new TopicExchange(priceUpdateExchange, true, false);
    }

    @Bean
    public Queue orderEventQueue() {
        return new Queue(orderEventExchange + "." + uniqueQueueName + "." + orderEventRoutingKey, true, false, false);
    }

    @Bean
    public Binding orderEventBinding() {
        return BindingBuilder.bind(orderEventQueue()).to(orderEventTopicExchange()).with(orderEventRoutingKey);
    }

    @Bean
    public Queue addProductQueue() {
        return new Queue(productExchange + "." + uniqueQueueName + "." + addProductRoutingKey, true, false, false);
    }

    @Bean
    public Binding addProductBinding() {
        return BindingBuilder.bind(addProductQueue()).to(productTopicExchange()).with(addProductRoutingKey);
    }

    @Bean
    public Queue updateProductQueue() {
        return new Queue(productExchange + "." + uniqueQueueName + "." + updateProductRoutingKey, true, false, false);
    }

    @Bean
    public Binding updateProductBinding() {
        return BindingBuilder.bind(updateProductQueue()).to(productTopicExchange()).with(updateProductRoutingKey);
    }

    @Bean
    public Queue deleteProductQueue() {
        return new Queue(productExchange + "." + uniqueQueueName + "." + deleteProductRoutingKey, true, false, false);
    }

    @Bean
    public Binding deleteProductBinding() {
        return BindingBuilder.bind(deleteProductQueue()).to(productTopicExchange()).with(deleteProductRoutingKey);
    }

}
