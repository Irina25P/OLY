package org.example.usermicroservice.services;

import org.example.usermicroservice.dtos.ClientDTO;
import org.example.usermicroservice.dtos.SellerDTO;
import org.example.usermicroservice.dtos.mapper.ClientMapper;
import org.example.usermicroservice.dtos.mapper.SellerMapper;
import org.example.usermicroservice.entities.Admin;
import org.example.usermicroservice.entities.Client;
import org.example.usermicroservice.entities.Seller;
import org.example.usermicroservice.messaging.producer.DeleteUserProducer;
import org.example.usermicroservice.repositories.AdminRepository;
import org.example.usermicroservice.repositories.ClientRepository;
import org.example.usermicroservice.repositories.SellerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminService {
    private final AdminRepository adminRepository;
    private final ClientRepository clientRepository;
    private final SellerRepository sellerRepository;

    public AdminService(AdminRepository adminRepository, ClientRepository clientRepository, 
                       SellerRepository sellerRepository) {
        this.adminRepository = adminRepository;
        this.clientRepository = clientRepository;
        this.sellerRepository = sellerRepository;
    }

    public void saveAdmin(Admin admin) {
        adminRepository.save(admin);
    }

    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll().stream()
                .map(ClientMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<SellerDTO> getAllSellers() {
        return sellerRepository.findAll().stream()
                .map(SellerMapper::toDTO)
                .collect(Collectors.toList());
    }
}