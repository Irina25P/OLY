package com.example.order.consumer;

import com.example.order.dto.UserDto;
import com.example.order.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;


@Component
public class UserConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserConsumer.class);

    private final UserService userService;
    private final ObjectMapper objectMapper;

    public UserConsumer(UserService userService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "${rabbitmq.sync.exchange.user.topic}.${rabbitmq.sync.queue.unique.name}.${rabbitmq.sync.routingkey.client}")
    public void consumeAddClientMessage(String message) {
        try{
            LOGGER.info("Received add client message from queue: {}", message);

            UserDto userDto = objectMapper.readValue(message, UserDto.class);

            userService.save(userDto);

            LOGGER.info("Added user with id: {}", userDto.getId());
        } catch(Exception e) {
            LOGGER.error("Error processing add client message: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "${rabbitmq.sync.exchange.user.topic}.${rabbitmq.sync.queue.unique.name}.${rabbitmq.sync.routingkey.seller}")
    public void consumeAddSellerMessage(String message) {
        try{
            LOGGER.info("Received add seller message from queue: {}", message);

            UserDto userDto = objectMapper.readValue(message, UserDto.class);

            userService.save(userDto);

            LOGGER.info("Added user with id: {}", userDto.getId());
        } catch (Exception e) {
            LOGGER.error("Error processing add seller message: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "${rabbitmq.sync.exchange.user.topic}.${rabbitmq.sync.queue.unique.name}.${rabbitmq.sync.routingkey.deleteUser}")
    public void consumeDeleteUserMessage(String message) {
        try{
            LOGGER.info("Received delete user message from queue: {}", message);

            UserDto userDto = objectMapper.readValue(message, UserDto.class);

            userService.deleteUser(userDto);

            LOGGER.info("Deleted user with id: {}", userDto.getId());
        } catch (Exception e) {
            LOGGER.error("Error processing delete user message: {}", e.getMessage());
        }
    }
}
