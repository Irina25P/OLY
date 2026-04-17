package com.example.authmicroservice.consumer;

import com.example.authmicroservice.dtos.DeleteUserMessageDTO;
import com.example.authmicroservice.services.UserService;
import tools.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DeleteUserConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteUserConsumer.class);

    private final UserService userService;
    private final ObjectMapper objectMapper;

    public DeleteUserConsumer(UserService userService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "${rabbitmq.sync.exchange.deleteUser.topic}.${rabbitmq.sync.queue.unique.name}")
    public void consumeDeleteUserMessage(String message) {
        try {
            LOGGER.info("Received deleteUser message: {}", message);

            DeleteUserMessageDTO deleteUserDTO = objectMapper.readValue(message, DeleteUserMessageDTO.class);

            userService.deleteUser(deleteUserDTO.getId());

            LOGGER.info("Successfully deleted user with id {}", deleteUserDTO.getId());
        } catch (Exception e) {
            LOGGER.error("Error processing deleteUser message: " + e.getMessage(), e);
        }
    }
}
