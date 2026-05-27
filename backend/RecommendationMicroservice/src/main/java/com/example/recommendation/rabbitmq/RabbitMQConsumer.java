package com.example.recommendation.rabbitmq;

import com.example.recommendation.rabbitmq.messages.OrderEventMessage;
import com.example.recommendation.rabbitmq.messages.ProductMessage;
import com.example.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQConsumer {

    private final ObjectMapper objectMapper;
    private final RecommendationService recommendationService;

    @RabbitListener(queues = "${rabbitmq.sync.exchange.order.event.topic}.${rabbitmq.sync.queue.unique.name}.${rabbitmq.sync.routingkey.orderEvent}")
    public void consumeOrderEvent(String message) {
        try {
            OrderEventMessage event = objectMapper.readValue(normalizePayload(message), OrderEventMessage.class);
            log.info("Received order event for client: {}", event.getClientId());
            recommendationService.processOrderEvent(event);
        } catch (Exception e) {
            log.error("Error processing order event message: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "${rabbitmq.sync.exchange.product.topic}.${rabbitmq.sync.queue.unique.name}.${rabbitmq.sync.routingkey.addProduct}")
    public void consumeAddProduct(String message) {
        try {
            ProductMessage msg = objectMapper.readValue(normalizePayload(message), ProductMessage.class);
            log.info("Received add product event for product: {}", msg.getId());
            recommendationService.upsertProduct(msg);
        } catch (Exception e) {
            log.error("Error processing add product message: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "${rabbitmq.sync.exchange.product.topic}.${rabbitmq.sync.queue.unique.name}.${rabbitmq.sync.routingkey.updateProduct}")
    public void consumeUpdateProduct(String message) {
        try {
            ProductMessage msg = objectMapper.readValue(normalizePayload(message), ProductMessage.class);
            log.info("Received update product event for product: {}", msg.getId());
            recommendationService.upsertProduct(msg);
        } catch (Exception e) {
            log.error("Error processing update product message: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "${rabbitmq.sync.exchange.product.topic}.${rabbitmq.sync.queue.unique.name}.${rabbitmq.sync.routingkey.deleteProduct}")
    public void consumeDeleteProduct(String message) {
        try {
            ProductMessage msg = objectMapper.readValue(normalizePayload(message), ProductMessage.class);
            log.info("Received delete product event for product: {}", msg.getId());
            if (msg.getId() != null) {
                recommendationService.deleteProduct(msg.getId().toString());
            }
        } catch (Exception e) {
            log.error("Error processing delete product message: {}", e.getMessage());
        }
    }

    private String normalizePayload(String message) throws Exception {
        if (message == null) {
            return null;
        }

        String trimmed = message.trim();
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
            return objectMapper.readValue(trimmed, String.class);
        }

        return message;
    }
}
