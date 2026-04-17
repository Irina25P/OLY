package com.example.authmicroservice.services;

import com.example.authmicroservice.dtos.AdminMessageDTO;
import com.example.authmicroservice.dtos.ClientMessageDTO;
import com.example.authmicroservice.dtos.RegisterAdminDTO;
import com.example.authmicroservice.dtos.RegisterClientDTO;
import com.example.authmicroservice.dtos.RegisterSellerDTO;
import com.example.authmicroservice.dtos.SellerMessageDTO;
import com.example.authmicroservice.dtos.mappers.AdminMessageMapper;
import com.example.authmicroservice.dtos.mappers.ClientMessageMapper;
import com.example.authmicroservice.dtos.mappers.RegisterAdminMapper;
import com.example.authmicroservice.dtos.mappers.RegisterClientMapper;
import com.example.authmicroservice.dtos.mappers.RegisterSellerMapper;
import com.example.authmicroservice.dtos.mappers.SellerMessageMapper;
import com.example.authmicroservice.entities.User;
import com.example.authmicroservice.entities.UserInfoDetails;
import com.example.authmicroservice.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${rabbitmq.sync.exchange.addUser.topic}")
    private String syncExchangeAddUserTopic;

    @Value("${rabbitmq.sync.routingkey.admin}")
    private String routingKeyAdmin;

    @Value("${rabbitmq.sync.routingkey.seller}")
    private String routingKeySeller;

    @Value("${rabbitmq.sync.routingkey.client}")
    private String routingKeyClient;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            RabbitTemplate rabbitTemplate,
            ObjectMapper objectMapper
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public User findByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public String addAdmin(RegisterAdminDTO dto) {
        User user = RegisterAdminMapper.from(dto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
        LOGGER.info("New Admin has been added: " + user.getUsername());

        AdminMessageDTO adminMessageDTO = AdminMessageMapper.from(dto, user.getId());

        try {
            String message = objectMapper.writeValueAsString(adminMessageDTO);
            rabbitTemplate.convertAndSend(syncExchangeAddUserTopic, routingKeyAdmin, message);
            LOGGER.info("Sent admin sync message for user with id {}", user.getId());
        } catch (Exception e) {
            LOGGER.error("Error sending admin sync message to RabbitMQ: " + e.getMessage());
        }
        return "Admin Added Successfully";
    }

    public String addSeller(RegisterSellerDTO dto) {
        User user = RegisterSellerMapper.from(dto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
        LOGGER.info("New Seller has been added: " + user.getUsername());

        SellerMessageDTO sellerMessageDTO = SellerMessageMapper.from(dto, user.getId());

        try {
            String message = objectMapper.writeValueAsString(sellerMessageDTO);
            rabbitTemplate.convertAndSend(syncExchangeAddUserTopic, routingKeySeller, message);
            LOGGER.info("Sent seller sync message for user with id {}", user.getId());
        } catch (Exception e) {
            LOGGER.error("Error sending seller sync message to RabbitMQ: " + e.getMessage());
        }
        return "Seller Added Successfully";
    }

    public String addClient(RegisterClientDTO dto) {
        User user = RegisterClientMapper.from(dto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
        LOGGER.info("New Client has been added: " + user.getUsername());

        ClientMessageDTO clientMessageDTO = ClientMessageMapper.from(dto, user.getId());

        try {
            String message = objectMapper.writeValueAsString(clientMessageDTO);
            rabbitTemplate.convertAndSend(syncExchangeAddUserTopic, routingKeyClient, message);
            LOGGER.info("Sent client sync message for user with id {}", user.getId());
        } catch (Exception e) {
            LOGGER.error("Error sending client sync message to RabbitMQ: " + e.getMessage());
        }
        return "Client Added Successfully";
    }

    public void deleteUser(UUID id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.deleteById(id);
            LOGGER.info("User with id {} has been successfully deleted in auth database", id);
        } else {
            LOGGER.error("User with id {} has not been found in auth database", id);
            throw new UsernameNotFoundException("User with id " + id + " not found");
        }
    }
}
