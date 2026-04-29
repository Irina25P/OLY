package org.example.usermicroservice.messaging.producer;

import org.example.usermicroservice.dtos.DeleteUserDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class DeleteUserProducer {

    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.sync.exchange.user}")
    private String deleteUserExchange;

    @Value("${rabbitmq.sync.routingkey.deleteuser}")
    private String deleteUserRoutingKey;

    public DeleteUserProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendDeleteUserMessage(DeleteUserDTO message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            rabbitTemplate.convertAndSend(deleteUserExchange, deleteUserRoutingKey, json);
            System.out.println("Delete user message sent for user: " + message.getId());
        } catch (Exception e) {
            System.err.println("Error sending delete user message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

