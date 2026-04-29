package org.example.usermicroservice.messaging.producer;

import org.example.usermicroservice.dtos.DeleteImagesDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class DeleteImageProducer {

    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.sync.exchange.deleteImage}")
    private String deleteImageExchange;

    @Value("${rabbitmq.sync.routingkey.deleteImage}")
    private String deleteImageRoutingKey;

    public DeleteImageProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendDeleteImageMessage(DeleteImagesDTO message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            rabbitTemplate.convertAndSend(deleteImageExchange, deleteImageRoutingKey, json);
            System.out.println("Delete image message sent");
        } catch (Exception e) {
            System.err.println("Error sending delete image message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

