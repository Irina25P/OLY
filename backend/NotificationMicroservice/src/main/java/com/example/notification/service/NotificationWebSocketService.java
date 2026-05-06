package com.example.notification.service;

import com.example.notification.dto.NotificationDto;
import com.example.notification.dto.mapper.NotificationMapper;
import com.example.notification.model.Notification;
import com.example.notification.model.User;
import com.example.notification.model.enums.Topic;
import com.example.notification.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationWebSocketService {

    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationWebSocketService(UserRepository userRepository, SimpMessagingTemplate messagingTemplate) {
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public void createAndSendNotification(NotificationDto notificationDto, Topic topic) {
        User user = userRepository.findById(notificationDto.getReceiverId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = NotificationMapper.toNotification(notificationDto);
        notification.setTopic(topic);

        user.getNotifications().add(notification);
        userRepository.save(user);

        notificationDto.setId(notification.getId());

        String destination = "/topic/user/" + user.getId() + topic.getDestinationPath();
        messagingTemplate.convertAndSend(destination, notificationDto);
    }
}

