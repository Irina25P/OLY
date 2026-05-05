package com.example.notification.dto.mapper;

import com.example.notification.dto.*;
import com.example.notification.model.Notification;

public class NotificationMapper {
    public static Notification toNotification(NotificationDto notificationDto) {
        Notification notification = new Notification();

        notification.setMessage(notificationDto.getMessage());
        notification.setSendAt(null);

        return notification;
    }
}
