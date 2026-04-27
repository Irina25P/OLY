package com.example.product.rabbitmq.consumer;

import com.example.product.rabbitmq.dto.OrderStockUpdateMessageDto;
import com.example.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockConsumer {


    private final ObjectMapper objectMapper;
    private final ProductService productService;

    @RabbitListener(queues = "${rabbitmq.sync.exchange.changeStock.topic}.${rabbitmq.sync.queue.unique.name}.${rabbitmq.sync.routingKey.changeStock}")
    public void receiveStockUpdate(String message) {
        try {
            OrderStockUpdateMessageDto receivedMessage = objectMapper.convertValue(message, OrderStockUpdateMessageDto.class);
            productService.updateStockFromOrder(receivedMessage);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}