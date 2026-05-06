package com.example.notification.controller;

import com.example.notification.dto.NotificationAckDto;
import com.example.notification.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.sql.Timestamp;

@Controller
public class NotificationAckController {

    private final UserRepository userRepository;

    public NotificationAckController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @MessageMapping("/notification.ack")
    @Transactional
    public void handleAck(NotificationAckDto ack) {
        userRepository.findById(ack.getReceiverId()).ifPresent(user -> {

            user.getNotifications().stream()
                    .filter(notif -> notif.getId().equals(ack.getNotificationId()))
                    .findFirst()
                    .ifPresent(notif -> {
                        notif.setSendAt(new Timestamp(System.currentTimeMillis()));
                        userRepository.save(user);
                        System.out.println("ACK received, notification 'sendAt' was set successfully");
                    });

        });
    }
}
