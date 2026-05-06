package com.example.notification.service;

import com.example.notification.model.User;
import com.example.notification.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class NotificationCleanupService {

    private final UserRepository userRepository;

    public NotificationCleanupService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanupReadNotifications() {
        log.info("Starting cleanup job for read notifications...");

        List<User> users = userRepository.findAll();

        for (User user : users) {
            int initialSize = user.getNotifications().size();

            user.getNotifications().removeIf(notification -> notification.getSendAt() != null);

            int finalSize = user.getNotifications().size();

            if (initialSize != finalSize) {
                userRepository.save(user);
                log.info("Deleted {} notifications for user {}", (initialSize - finalSize), user.getId());
            }
        }

        log.info("Cleanup job finished successfully.");
    }
}