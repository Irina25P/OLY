package org.example.usermicroservice.messaging.consumer;

import org.example.usermicroservice.dtos.AdminDTO;
import org.example.usermicroservice.dtos.mapper.AdminMapper;
import org.example.usermicroservice.entities.Admin;
import org.example.usermicroservice.services.AdminService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class AdminMessageConsumer {

    @Value("${rabbitmq.sync.unique.name}")
    private String uniqueName;

    private final AdminService adminService;
    private final ObjectMapper objectMapper;

    public AdminMessageConsumer(AdminService adminService, ObjectMapper objectMapper) {
        this.adminService = adminService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "${rabbitmq.sync.exchange.user}.${rabbitmq.sync.unique.name}.${rabbitmq.sync.routingkey.admin}")
    public void consumeAdminMessage(String message) {
        try {
            AdminDTO adminDTO = objectMapper.readValue(message, AdminDTO.class);
            Admin admin = AdminMapper.toEntity(adminDTO);
            adminService.saveAdmin(admin);
            System.out.println("Admin saved from RabbitMQ: " + adminDTO.getId());
        } catch (Exception e) {
            System.err.println("Error processing admin message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

