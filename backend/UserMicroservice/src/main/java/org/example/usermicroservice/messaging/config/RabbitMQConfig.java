package org.example.usermicroservice.messaging.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.sync.exchange.deleteuser}")
    private String deleteUserExchange;

    @Value("${rabbitmq.sync.exchange.adduser}")
    private String addUserExchange;

    @Value("${rabbitmq.sync.exchange.deleteimage}")
    private String deleteImageExchange;

    @Value("${rabbitmq.sync.routingkey.admin}")
    private String adminRoutingKey;

    @Value("${rabbitmq.sync.routingkey.seller}")
    private String sellerRoutingKey;

    @Value("${rabbitmq.sync.routingkey.client}")
    private String clientRoutingKey;

    @Value("${rabbitmq.sync.unique.name}")
    private String uniqueName;

    @Bean
    public TopicExchange deleteUserExchange() {
        return new TopicExchange(deleteUserExchange, true, false);
    }

    @Bean
    public TopicExchange addUserExchange() {
        return new TopicExchange(addUserExchange, true, false);
    }

    @Bean
    public TopicExchange deleteImageExchange() {
        return new TopicExchange(deleteImageExchange, true, false);
    }

    @Bean
    public Queue adminQueue() {
        return new Queue(addUserExchange + "." + uniqueName + "." + adminRoutingKey, true);
    }

    @Bean
    public Binding adminBinding(Queue adminQueue, TopicExchange addUserExchange) {
        return BindingBuilder.bind(adminQueue)
                .to(addUserExchange)
                .with(adminRoutingKey);
    }

    @Bean
    public Queue sellerQueue() {
        return new Queue(addUserExchange + "." + uniqueName + "." + sellerRoutingKey, true);
    }

    @Bean
    public Binding sellerBinding(Queue sellerQueue, TopicExchange addUserExchange) {
        return BindingBuilder.bind(sellerQueue)
                .to(addUserExchange)
                .with(sellerRoutingKey);
    }

    @Bean
    public Queue clientQueue() {
        return new Queue(addUserExchange + "." + uniqueName + "." + clientRoutingKey, true);
    }

    @Bean
    public Binding clientBinding(Queue clientQueue, TopicExchange addUserExchange) {
        return BindingBuilder.bind(clientQueue)
                .to(addUserExchange)
                .with(clientRoutingKey);
    }

    @Bean
    public Queue deleteImageQueue() {
        return new Queue("deleteImage-Topic-Exchange." + uniqueName + ".image", true);
    }

    @Bean
    public Binding deleteImageBinding(Queue deleteImageQueue, TopicExchange deleteImageExchange) {
        return BindingBuilder.bind(deleteImageQueue)
                .to(deleteImageExchange)
                .with("deleteImage");
    }
}

