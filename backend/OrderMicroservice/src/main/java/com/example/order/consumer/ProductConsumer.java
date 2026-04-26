package com.example.order.consumer;

import com.example.order.dto.ProductDto;
import com.example.order.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class ProductConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductConsumer.class);

    private final ProductService productService;
    private final ObjectMapper objectMapper;

    public ProductConsumer(ProductService productService, ObjectMapper objectMapper) {
        this.productService = productService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "${rabbitmq.sync.exchange.product.topic}.${rabbitmq.sync.queue.unique.name}.${rabbitmq.sync.routingkey.addProduct}")
    public void consumeAddProductMessage(String message) {
        try {
            LOGGER.info("Received add product message from queue: {}", message);

            ProductDto productDto = objectMapper.readValue(message, ProductDto.class);

            productService.save(productDto);

            LOGGER.info("Added product with id: {}", productDto.getId());
        } catch (Exception e) {
            LOGGER.error("Error processing add product message: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "${rabbitmq.sync.exchange.product.topic}.${rabbitmq.sync.queue.unique.name}.${rabbitmq.sync.routingkey.updateProduct}")
    public void consumeUpdateProductMessage(String message) {
        try {
            LOGGER.info("Received update product message from queue: {}", message);

            ProductDto productDto = objectMapper.readValue(message, ProductDto.class);

            productService.update(productDto);

            LOGGER.info("Updated product with id: {}", productDto.getId());
        } catch (Exception e) {
            LOGGER.error("Error processing update product message: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "${rabbitmq.sync.exchange.product.topic}.${rabbitmq.sync.queue.unique.name}.${rabbitmq.sync.routingkey.deleteProduct}")
    public void consumeDeleteProductMessage(String message) {
        try {
            LOGGER.info("Received delete product message from queue: {}", message);

            ProductDto productDto = objectMapper.readValue(message, ProductDto.class);

            productService.delete(productDto);

            LOGGER.info("Deleted product with id: {}", productDto.getId());
        } catch (Exception e) {
            LOGGER.error("Error processing delete product message: {}", e.getMessage());
        }
    }
}
