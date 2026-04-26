package com.example.order.service;

import com.example.order.dto.UserDto;
import com.example.order.entities.User;
import com.example.order.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void save(UserDto userDto) {
        User user = new User(userDto.getId());
        userRepository.save(user);
    }

    public void deleteUser(UserDto userDto) {
        User user = userRepository.findById(userDto.getId()).orElseThrow(
                () -> new EntityNotFoundException("User with id " + userDto.getId() + " not found"));

        userRepository.delete(user);
    }
}
