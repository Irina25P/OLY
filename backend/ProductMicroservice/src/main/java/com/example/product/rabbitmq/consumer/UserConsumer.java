package com.example.product.rabbitmq.consumer;

import com.example.product.rabbitmq.dto.UserDto;
import com.example.product.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserConsumer {

    private final ObjectMapper objectMapper;
    private final UserService userService;

    @RabbitListener(queues = "${rabbitmq.sync.exchange.user.topic}.${rabbitmq.sync.queue.unique.name}.${rabbitmq.sync.routingkey.client}")
    public void consumeAddClientMessage(String message) {
        try {
            UserDto userDto = objectMapper.readValue(message, UserDto.class);
            log.info("Received add client message: {}", userDto);

            userService.save(userDto);

            log.info("Added client with id: {}", userDto.getId());
        } catch (Exception e) {
            log.error("Error processing add client message: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "${rabbitmq.sync.exchange.user.topic}.${rabbitmq.sync.queue.unique.name}.${rabbitmq.sync.routingkey.seller}")
    public void consumeAddSellerMessage(String message) {
        try {
            UserDto userDto = objectMapper.readValue(message, UserDto.class);
            log.info("Received add seller message: {}", userDto);

            userService.save(userDto);

            log.info("Added seller with id: {}", userDto.getId());
        } catch (Exception e) {
            log.error("Error processing add seller message: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "${rabbitmq.sync.exchange.user.topic}.${rabbitmq.sync.queue.unique.name}.${rabbitmq.sync.routingkey.deleteUser}")
    public void consumeDeleteUserMessage(String message) {
        try {
            UserDto userDto = objectMapper.readValue(message, UserDto.class);
            log.info("Received delete user message: {}", userDto);

            userService.deleteUser(userDto);

            log.info("Deleted user with id: {}", userDto.getId());
        } catch (Exception e) {
            log.error("Error processing delete user message: {}", e.getMessage());
        }
    }
}