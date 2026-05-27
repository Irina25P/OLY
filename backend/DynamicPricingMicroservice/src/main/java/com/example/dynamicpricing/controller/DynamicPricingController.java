package com.example.dynamicpricing.controller;

import com.example.dynamicpricing.dto.PriceRecommendationDto;
import com.example.dynamicpricing.service.DynamicPricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/pricing")
@RequiredArgsConstructor
public class DynamicPricingController {

    private final DynamicPricingService dynamicPricingService;

    /**
     * Returns price recommendations for all products of the authenticated seller.
     */
    @PreAuthorize("hasRole('SELLER')")
    @GetMapping
    public ResponseEntity<List<PriceRecommendationDto>> getSellerRecommendations(
            @RequestHeader(name = "${security.user-id-header}") String sellerId) {
        return ResponseEntity.ok(dynamicPricingService.getRecommendationsForSeller(UUID.fromString(sellerId)));
    }

    /**
     * Returns the price recommendation for a specific product.
     */
    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/{productId}")
    public ResponseEntity<PriceRecommendationDto> getProductRecommendation(
            @PathVariable UUID productId,
            @RequestHeader(name = "${security.user-id-header}") String sellerId) {
        return ResponseEntity.ok(
                dynamicPricingService.getRecommendationForProduct(productId, UUID.fromString(sellerId))
        );
    }

    /**
     * Applies the recommended price to the product (publishes price update event).
     */
    @PreAuthorize("hasRole('SELLER')")
    @PostMapping("/{productId}/apply")
    public ResponseEntity<PriceRecommendationDto> applyRecommendation(
            @PathVariable UUID productId,
            @RequestHeader(name = "${security.user-id-header}") String sellerId) {
        return ResponseEntity.ok(
                dynamicPricingService.applyRecommendation(productId, UUID.fromString(sellerId))
        );
    }
}
