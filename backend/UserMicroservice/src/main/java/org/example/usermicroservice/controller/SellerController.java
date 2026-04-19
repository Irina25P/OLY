package org.example.usermicroservice.controller;

import org.example.usermicroservice.dtos.SellerDTO;
import org.example.usermicroservice.services.SellerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/users/seller")
public class SellerController {
    private final SellerService sellerService;


    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/profile")
    public ResponseEntity<SellerDTO> getProfile(@RequestHeader(name = "${security.user-id-header}") String userId) {
        try {
            return ResponseEntity.ok(sellerService.getSellerProfile(UUID.fromString(userId)));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('SELLER')")
    @PutMapping("/profile")
    public ResponseEntity<SellerDTO> updateProfile(@RequestHeader(name = "${security.user-id-header}") String userId,
                                                    @RequestBody SellerDTO sellerDTO) {
        try {
            return ResponseEntity.ok(sellerService.updateSellerProfile(UUID.fromString(userId), sellerDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('SELLER')")
    @DeleteMapping("/profile")
    public ResponseEntity<Void> deleteProfile(@RequestHeader(name = "${security.user-id-header}") String userId) {
        try {
            sellerService.deleteSellerProfile(UUID.fromString(userId));
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}