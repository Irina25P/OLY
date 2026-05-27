package com.example.notification.consumer;

import com.example.notification.dto.NotificationDto;
import com.example.notification.model.enums.Topic;
import com.example.notification.service.NotificationWebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class NotificationConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationConsumer.class);

    private final NotificationWebSocketService notificationWebSocketService;
    private final ObjectMapper objectMapper;

    public NotificationConsumer(NotificationWebSocketService notificationWebSocketService, ObjectMapper objectMapper) {
        this.notificationWebSocketService = notificationWebSocketService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "#{orderCreatedQueue.name}")
    public void consumeOrderCreated(String message) {
        try {
            LOGGER.info("Received add product message from queue: {}", message);

            NotificationDto notificationDto = objectMapper.readValue(normalizePayload(message), NotificationDto.class);

            notificationWebSocketService.createAndSendNotification(notificationDto, Topic.RECEIVED_ORDER);
        } catch (Exception e) {
            LOGGER.error("Error processing add product message: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "#{orderUpdatedQueue.name}")
    public void consumeOrderUpdated(String message) {
        try {
            LOGGER.info("Received add product message from queue: {}", message);

            NotificationDto notificationDto = objectMapper.readValue(normalizePayload(message), NotificationDto.class);

            notificationWebSocketService.createAndSendNotification(notificationDto, Topic.UPDATE_ORDER);
        } catch (Exception e) {
            System.err.println("Eroare la procesarea notificării orderUpdated: " + e.getMessage());
        }
    }

    @RabbitListener(queues = "#{reviewCreatedQueue.name}")
    public void consumeReviewCreated(String message) {
        try {
            LOGGER.info("Received add product message from queue: {}", message);

            NotificationDto notificationDto = objectMapper.readValue(normalizePayload(message), NotificationDto.class);

            notificationWebSocketService.createAndSendNotification(notificationDto, Topic.RECEIVED_REVIEW);
        } catch (Exception e) {
            System.err.println("Eroare la procesarea notificării reviewCreated: " + e.getMessage());
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
