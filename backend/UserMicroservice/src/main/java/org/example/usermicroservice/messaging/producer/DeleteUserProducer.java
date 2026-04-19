package org.example.usermicroservice.messaging.producer;

import org.example.usermicroservice.dtos.DeleteUserDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DeleteUserProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.sync.exchange.deleteuser:deleteUser-Topic-Exchange}")
    private String deleteUserExchange;

    @Value("${rabbitmq.sync.routingkey.deleteuser:deleteUser}")
    private String deleteUserRoutingKey;

    public DeleteUserProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendDeleteUserMessage(DeleteUserDTO message) {
        try {
            rabbitTemplate.convertAndSend(deleteUserExchange, deleteUserRoutingKey, message);
            System.out.println("Delete user message sent for user: " + message.getId());
        } catch (Exception e) {
            System.err.println("Error sending delete user message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

