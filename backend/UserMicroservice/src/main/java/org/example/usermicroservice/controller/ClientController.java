package org.example.usermicroservice.controller;

import org.example.usermicroservice.dtos.ClientDTO;
import org.example.usermicroservice.services.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/users/client")
public class ClientController {
    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/profile")
    public ResponseEntity<ClientDTO> getProfile(@RequestHeader(name = "${security.user-id-header}") String userId) {
        try {
            return ResponseEntity.ok(clientService.getClientProfile(UUID.fromString(userId)));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('CLIENT')")
    @PutMapping("/profile")
    public ResponseEntity<ClientDTO> updateProfile(@RequestHeader(name = "${security.user-id-header}") String userId,
                                                    @RequestBody ClientDTO clientDTO) {
        try {
            return ResponseEntity.ok(clientService.updateClientProfile(UUID.fromString(userId), clientDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('CLIENT')")
    @DeleteMapping("/profile")
    public ResponseEntity<Void> deleteProfile(@RequestHeader(name = "${security.user-id-header}") String userId) {
        try {
            clientService.deleteClientProfile(UUID.fromString(userId));
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
