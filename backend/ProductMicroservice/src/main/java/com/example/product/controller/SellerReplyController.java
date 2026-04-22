package com.example.product.controller;

import com.example.product.dto.CreateSellerReplyRequest;
import com.example.product.dto.SellerReplyDetailDTO;
import com.example.product.service.SellerReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/seller-replies")
@RequiredArgsConstructor
public class SellerReplyController {
    private final SellerReplyService sellerReplyService;

    @PostMapping
    public ResponseEntity<SellerReplyDetailDTO> createReply(
            @RequestHeader(name = "${security.user-id-header}") UUID userId,
            @RequestBody CreateSellerReplyRequest request) {
        SellerReplyDetailDTO replyDTO = sellerReplyService.createReply(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(replyDTO);
    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<Void> deleteReply(
            @RequestHeader(name = "${security.user-id-header}") UUID userId,
            @PathVariable UUID replyId) {
        sellerReplyService.deleteReply(replyId, userId);
        return ResponseEntity.noContent().build();
    }
}

