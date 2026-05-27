package com.example.dynamicpricing.rabbitmq;

import com.example.dynamicpricing.rabbitmq.messages.PriceUpdateMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceUpdateProducer {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${rabbitmq.sync.exchange.price.update.topic}")
    private String priceUpdateExchange;

    @Value("${rabbitmq.sync.routingkey.priceUpdate}")
    private String priceUpdateRoutingKey;

    public void sendPriceUpdate(UUID productId, Double newPrice) {
        try {
            PriceUpdateMessage message = new PriceUpdateMessage(productId, newPrice);
            String json = objectMapper.writeValueAsString(message);
            rabbitTemplate.convertAndSend(priceUpdateExchange, priceUpdateRoutingKey, json);
            log.info("Sent price update for product {}: new price {}", productId, newPrice);
        } catch (Exception e) {
            log.error("Error sending price update for product {}: {}", productId, e.getMessage());
        }
    }
}
