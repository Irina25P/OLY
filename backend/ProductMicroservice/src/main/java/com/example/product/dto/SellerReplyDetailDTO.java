package com.example.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerReplyDetailDTO {
    private UUID id;
    private UUID sellerId;
    private String reply;
    private LocalDateTime createdAt;
}