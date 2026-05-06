package com.example.notification.service;

import com.example.notification.dto.UserDto;
import com.example.notification.model.Notification;
import com.example.notification.model.User;
import com.example.notification.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void save(UserDto userDto) {
        List<Notification> notifications = new ArrayList<Notification>();
        User user = new User(userDto.getId(),notifications);

        userRepository.save(user);
    }

    public void deleteUser(UserDto userDto) {
        userRepository.deleteById(userDto.getId());
    }
}
