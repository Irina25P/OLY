package com.example.notification.listener;

import com.example.notification.model.Notification;
import com.example.notification.model.enums.Topic;
import com.example.notification.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.List;
import java.util.UUID;

@Component
public class WebSocketEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketEventListener.class);

    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketEventListener(UserRepository userRepository, SimpMessagingTemplate messagingTemplate) {
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    @Transactional(readOnly = true)
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination();

        // Destinația așteptată are formatul: /topic/user/{userId}/{topicName}
        // Exemplu: /topic/user/123e4567-e89b-12d3-a456-426614174000/receivedOrder
        if (destination != null && destination.startsWith("/topic/user/")) {
            try {
                String[] parts = destination.split("/");

                if (parts.length >= 5) {

                    UUID userId = UUID.fromString(parts[3]);
                    String topicPath = "/" + parts[4];

                    Topic subscribedTopic = Topic.fromPath(topicPath);

                    sendPendingNotifications(userId, subscribedTopic, destination);
                }
            } catch (IllegalArgumentException e) {
                LOGGER.debug("Canal ignorat de sistemul de notificări (din urmă): {}", destination);
            } catch (Exception e) {
                LOGGER.error("Eroare la procesarea abonării WebSocket: {}", e.getMessage());
            }
        }
    }

    private void sendPendingNotifications(UUID userId, Topic subscribedTopic, String destination) {
        userRepository.findById(userId).ifPresent(user -> {

            List<Notification> pendingNotifications = user.getNotifications().stream()
                    .filter(notif -> notif.getSendAt() == null)
                    .filter(notif -> notif.getTopic() == subscribedTopic)
                    .toList();

            if (!pendingNotifications.isEmpty()) {
                LOGGER.info("Trimitere {} notificări restante pentru userul {} pe topicul {}",
                        pendingNotifications.size(), userId, subscribedTopic);

                for (Notification notification : pendingNotifications) {
                    messagingTemplate.convertAndSend(destination, notification);
                }
            }
        });
    }
}