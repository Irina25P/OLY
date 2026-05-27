package com.example.recommendation.controller;

import com.example.recommendation.dto.ProductRecommendationDto;
import com.example.recommendation.dto.TrackViewRequest;
import com.example.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @Value("${security.user-id-header}")
    private String userIdHeader;

    /**
     * Returns personalized product recommendations for the authenticated user.
     */
    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping
    public ResponseEntity<List<ProductRecommendationDto>> getRecommendations(
            @RequestHeader(name = "${security.user-id-header}") String userId) {
        return ResponseEntity.ok(recommendationService.getRecommendations(userId));
    }

    /**
     * Tracks a product view to improve future recommendations.
     */
    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/view")
    public ResponseEntity<Void> trackView(
            @RequestHeader(name = "${security.user-id-header}") String userId,
            @RequestBody TrackViewRequest request) {
        recommendationService.recordView(userId, request.getProductId());
        return ResponseEntity.ok().build();
    }
}
