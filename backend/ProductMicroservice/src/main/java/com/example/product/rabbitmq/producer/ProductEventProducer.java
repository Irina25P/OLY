package com.example.product.rabbitmq.producer;

import com.example.product.rabbitmq.dto.ChangedStockMessageDto;
import com.example.product.rabbitmq.dto.DeleteImageMessageDto;
import com.example.product.rabbitmq.dto.NotificationMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductEventProducer {

    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.sync.exchange.product.topic}")
    private String productExchange;

    @Value("${rabbitmq.sync.routingkey.addProduct}")
    private String addProductRoutingKey;

    @Value("${rabbitmq.sync.routingkey.updateProduct}")
    private String updateProductRoutingKey;

    @Value("${rabbitmq.sync.routingkey.deleteProduct}")
    private String deleteProductRoutingKey;

    @Value("${rabbitmq.sync.exchange.deleteImage.topic}")
    private String deleteImageExchange;

    @Value("${rabbitmq.sync.routingkey.deleteImage}")
    private String deleteImageRoutingKey;

    @Value("${rabbitmq.sync.exchange.notification.topic}")
    private String notificationExchange;

    @Value("${rabbitmq.sync.routingkey.reviewCreated}")
    private String reviewCreatedRoutingKey;

    public void sendImageDeleted(DeleteImageMessageDto message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            rabbitTemplate.convertAndSend(deleteImageExchange, deleteImageRoutingKey, json);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void sendProductCreated(ChangedStockMessageDto message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            rabbitTemplate.convertAndSend(productExchange, addProductRoutingKey, json);
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void sendProductUpdated(ChangedStockMessageDto message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            rabbitTemplate.convertAndSend(productExchange, updateProductRoutingKey, json);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void sendProductDeleted(ChangedStockMessageDto message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            rabbitTemplate.convertAndSend(productExchange, deleteProductRoutingKey, json);
        }  catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void sendReviewCreatedNotification(NotificationMessageDto message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            rabbitTemplate.convertAndSend(notificationExchange, reviewCreatedRoutingKey, json);
        }  catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}