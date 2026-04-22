package com.example.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDetailDTO {
    private UUID id;
    private Integer rating;
    private String comment;
    private UUID clientId;
    private LocalDateTime createdAt;
    private SellerReplyDetailDTO sellerReply;
}


