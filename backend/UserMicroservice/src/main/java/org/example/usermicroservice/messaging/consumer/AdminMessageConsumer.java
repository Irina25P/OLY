package org.example.usermicroservice.messaging.consumer;

import org.example.usermicroservice.dtos.AdminDTO;
import org.example.usermicroservice.dtos.mapper.AdminMapper;
import org.example.usermicroservice.entities.Admin;
import org.example.usermicroservice.services.AdminService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AdminMessageConsumer {

    @Value("${rabbitmq.sync.unique.name}")
    private String uniqueName;

    private final AdminService adminService;

    public AdminMessageConsumer(AdminService adminService) {
        this.adminService = adminService;
    }

    @RabbitListener(queues = "${rabbitmq.sync.exchange.adduser}.${rabbitmq.sync.unique.name}.${rabbitmq.sync.routingkey.admin}")
    public void consumeAdminMessage(AdminDTO message) {
        try {
            Admin admin = AdminMapper.toEntity(message);
            adminService.saveAdmin(admin);
            System.out.println("Admin saved from RabbitMQ: " + message.getId());
        } catch (Exception e) {
            System.err.println("Error processing admin message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

