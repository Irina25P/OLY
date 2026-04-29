package com.example.product.controller;

import com.example.product.dto.CreateReviewRequest;
import com.example.product.dto.CreateSellerReplyRequest;
import com.example.product.dto.ReviewDetailDTO;
import com.example.product.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ReviewDetailDTO> createReview(
            @RequestHeader(name = "${security.user-id-header}") UUID userId,
            @RequestBody CreateReviewRequest request) {
        ReviewDetailDTO reviewDTO = reviewService.createReview(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewDTO);
    }

    @GetMapping("/product/{productId}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('SELLER')")
    public ResponseEntity<List<ReviewDetailDTO>> getReviewsByProduct(@PathVariable UUID productId) {
        List<ReviewDetailDTO> reviews = reviewService.getReviewsByProductId(productId);
        return ResponseEntity.ok(reviews);
    }


    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> deleteReview(
            @RequestHeader(name = "${security.user-id-header}") UUID userId,
            @PathVariable UUID reviewId) {
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.noContent().build();
    }
}

