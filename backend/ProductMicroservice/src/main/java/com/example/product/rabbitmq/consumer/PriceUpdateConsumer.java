package com.example.product.rabbitmq.consumer;

import com.example.product.rabbitmq.dto.PriceUpdateMessageDto;
import com.example.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriceUpdateConsumer {

    private final ObjectMapper objectMapper;
    private final ProductService productService;

    @RabbitListener(queues = "${rabbitmq.sync.exchange.price.update.topic}.${rabbitmq.sync.queue.unique.name}.${rabbitmq.sync.routingkey.priceUpdate}")
    public void consumePriceUpdateMessage(String message) {
        try {
            PriceUpdateMessageDto dto = objectMapper.readValue(normalizePayload(message), PriceUpdateMessageDto.class);
            log.info("Received price update message for product id: {}", dto.getProductId());
            productService.applyDynamicPrice(dto.getProductId(), dto.getNewPrice());
            log.info("Applied dynamic price {} to product id: {}", dto.getNewPrice(), dto.getProductId());
        } catch (Exception e) {
            log.error("Error processing price update message: {}", e.getMessage());
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
