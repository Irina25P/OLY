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

    @Value("${rabbitmq.sync.exchange.user}")
    private String userExchange;

    @Value("${rabbitmq.sync.exchange.deleteImage}")
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
    public TopicExchange userExchange() {
        return new TopicExchange(userExchange, true, false);
    }

    @Bean
    public TopicExchange deleteImageExchange() {
        return new TopicExchange(deleteImageExchange, true, false);
    }

    @Bean
    public Queue adminQueue() {
        return new Queue(userExchange + "." + uniqueName + "." + adminRoutingKey, true);
    }

    @Bean
    public Binding adminBinding() {
        return BindingBuilder.bind(adminQueue())
                .to(userExchange())
                .with(adminRoutingKey);
    }

    @Bean
    public Queue sellerQueue() {
        return new Queue(userExchange + "." + uniqueName + "." + sellerRoutingKey, true);
    }

    @Bean
    public Binding sellerBinding() {
        return BindingBuilder.bind(sellerQueue())
                .to(userExchange())
                .with(sellerRoutingKey);
    }

    @Bean
    public Queue clientQueue() {
        return new Queue(userExchange + "." + uniqueName + "." + clientRoutingKey, true);
    }

    @Bean
    public Binding clientBinding() {
        return BindingBuilder.bind(clientQueue())
                .to(userExchange())
                .with(clientRoutingKey);
    }
}

