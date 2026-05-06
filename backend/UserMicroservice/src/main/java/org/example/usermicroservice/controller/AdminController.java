package org.example.usermicroservice.controller;

import org.example.usermicroservice.dtos.ClientDTO;
import org.example.usermicroservice.dtos.SellerDTO;
import org.example.usermicroservice.services.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/clients")
    public ResponseEntity<List<ClientDTO>> getAllClients() {
        return ResponseEntity.ok(adminService.getAllClients());
    }

   @GetMapping("/sellers")
    public ResponseEntity<List<SellerDTO>> getAllSellers() {
        return ResponseEntity.ok(adminService.getAllSellers());
    }

}

