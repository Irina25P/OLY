package com.example.product.service;

import com.example.product.entity.User;
import com.example.product.rabbitmq.dto.UserDto;
import com.example.product.repository.ProductRepository;
import com.example.product.repository.ReviewRepository;
import com.example.product.repository.SellerReplyRepository;
import com.example.product.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final SellerReplyRepository sellerReplyRepository;

    public void save(UserDto userDto) {
        if (userDto.getId() == null) {
            throw new RuntimeException("User id is required");
        }

        if (userRepository.existsById(userDto.getId())) {
            return;
        }

        userRepository.save(new User(userDto.getId(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
    }

    @Transactional
    public void deleteUser(UserDto userDto) {
        if (userDto.getId() == null) {
            throw new RuntimeException("User id is required");
        }

        UUID userId = userDto.getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.deleteById(userId);
    }
}