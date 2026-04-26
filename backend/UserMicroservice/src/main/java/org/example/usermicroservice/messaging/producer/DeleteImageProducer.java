package org.example.usermicroservice.messaging.producer;

import org.example.usermicroservice.dtos.DeleteImagesDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DeleteImageProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.sync.exchange.deleteimage:deleteImage-Topic-Exchange}")
    private String deleteImageExchange;

    @Value("${rabbitmq.sync.routingkey.deleteimage:deleteImage}")
    private String deleteImageRoutingKey;

    public DeleteImageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendDeleteImageMessage(DeleteImagesDTO message) {
        try {
            rabbitTemplate.convertAndSend(deleteImageExchange, deleteImageRoutingKey, message);
            System.out.println("Delete image message sent");
        } catch (Exception e) {
            System.err.println("Error sending delete image message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

