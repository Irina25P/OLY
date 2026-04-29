package org.example.usermicroservice.messaging.consumer;

import org.example.usermicroservice.dtos.SellerDTO;
import org.example.usermicroservice.dtos.mapper.SellerMapper;
import org.example.usermicroservice.entities.Seller;
import org.example.usermicroservice.services.SellerService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class SellerMessageConsumer {

    private final SellerService sellerService;
    private final ObjectMapper objectMapper;

    public SellerMessageConsumer(SellerService sellerService,  ObjectMapper objectMapper) {
        this.sellerService = sellerService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "${rabbitmq.sync.exchange.user}.${rabbitmq.sync.unique.name}.${rabbitmq.sync.routingkey.seller}")
    public void consumeSellerMessage(String message) {
        try {
            SellerDTO sellerDTO = objectMapper.readValue(message, SellerDTO.class);
            Seller seller = SellerMapper.toEntity(sellerDTO);
            sellerService.saveSellerProfile(seller);
            System.out.println("Seller saved from RabbitMQ: " + seller.getId());
        } catch (Exception e) {
            System.err.println("Error processing seller message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

