package com.example.order.service;

import com.example.order.dto.UserDto;
import com.example.order.entities.User;
import com.example.order.repository.OrderRepository;
import com.example.order.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public UserService(UserRepository userRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    public void save(UserDto userDto) {
        User user = new User(userDto.getId());
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(UserDto userDto) {
        UUID userId = userDto.getId();
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User with id " + userId + " not found"));

        // Delete all orders where the user is a client
        orderRepository.deleteByClient_Id(userId);

        // Delete all orders where the user is a seller
        orderRepository.deleteBySeller_Id(userId);

        // Delete the user itself
        userRepository.delete(user);
    }
}
