package com.example.notification.messaging;

import com.example.notification.dto.NotificationDto;
import com.example.notification.service.NotificationWebSocketService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationQueueListener {

    private final NotificationWebSocketService notificationWebSocketService;

    public NotificationQueueListener(NotificationWebSocketService notificationWebSocketService) {
        this.notificationWebSocketService = notificationWebSocketService;
    }

    @RabbitListener(queues = {
            "#{orderCreatedQueue.name}",
            "#{orderUpdatedQueue.name}",
            "#{reviewCreatedQueue.name}"
    })
    public void onNotification(NotificationDto notificationDto) {
        notificationWebSocketService.sendToUser(notificationDto);
    }
}

