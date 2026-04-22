package org.example.usermicroservice.services;

import org.example.usermicroservice.entities.Admin;
import org.example.usermicroservice.messaging.producer.DeleteUserProducer;
import org.example.usermicroservice.repositories.AdminRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository, DeleteUserProducer deleteUserProducer) {
        this.adminRepository = adminRepository;
    }

    public void saveAdmin(Admin admin) {
        adminRepository.save(admin);
    }
}