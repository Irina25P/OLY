package org.example.usermicroservice.messaging.consumer;

import org.example.usermicroservice.dtos.SellerDTO;
import org.example.usermicroservice.dtos.mapper.SellerMapper;
import org.example.usermicroservice.entities.Seller;
import org.example.usermicroservice.services.SellerService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class SellerMessageConsumer {

    private final SellerService sellerService;

    public SellerMessageConsumer(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @RabbitListener(queues = "${rabbitmq.sync.exchange.adduser}.${rabbitmq.sync.unique.name}.${rabbitmq.sync.routingkey.seller}")
    public void consumeSellerMessage(SellerDTO message) {
        try {
            Seller seller = SellerMapper.toEntity(message);
            sellerService.saveSellerProfile(seller);
            System.out.println("Seller saved from RabbitMQ: " + message.getId());
        } catch (Exception e) {
            System.err.println("Error processing seller message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

