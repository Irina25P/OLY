package com.example.notification.service;

import com.example.notification.dto.NotificationDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class NotificationWebSocketService {

    private static final String USER_NOTIFICATION_DESTINATION = "/queue/notifications";

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationWebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendToUser(NotificationDto notificationDto) {
        if (notificationDto == null || !StringUtils.hasText(notificationDto.getReceiverId())) {
            return;
        }

        messagingTemplate.convertAndSendToUser(
                notificationDto.getReceiverId(),
                USER_NOTIFICATION_DESTINATION,
                notificationDto
        );
    }
}

