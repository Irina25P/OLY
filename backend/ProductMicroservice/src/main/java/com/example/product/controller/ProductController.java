package com.example.product.controller;
import org.springframework.security.access.prepost.PreAuthorize;

import com.example.product.dto.*;
import com.example.product.entity.*;
import com.example.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<InterestType> categories,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minRating
    ) {
        return ResponseEntity.ok(
                productService.getAllProducts(name, categories, minPrice, maxPrice, minRating)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PreAuthorize("hasRole('SELLER')")
    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@RequestHeader(name = "${security.user-id-header}") String userId,
                                                            @RequestBody ProductRequestDto dto) {
        UUID sellerId = UUID.fromString(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(dto, sellerId));
    }

    @PreAuthorize("hasRole('SELLER')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(@RequestHeader(name = "${security.user-id-header}") String userId,
            @PathVariable UUID id,
            @RequestBody ProductRequestDto dto
    ) {
        UUID sellerId = UUID.fromString(userId);
        return ResponseEntity.ok(productService.updateProduct(id, dto, sellerId));
    }

    @PreAuthorize("hasRole('SELLER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@RequestHeader(name = "${security.user-id-header}") String userId,
                                              @PathVariable UUID id) {
        UUID sellerId = UUID.fromString(userId);
        productService.deleteProduct(id, sellerId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('SELLER')")
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductResponseDto> updateStock(@RequestHeader(name = "${security.user-id-header}") String userId,
            @PathVariable UUID id,
            @RequestParam Integer stock
    ) {
        UUID sellerId = UUID.fromString(userId);
        return ResponseEntity.ok(productService.updateStock(id, stock, sellerId));
    }

    @PreAuthorize("hasRole('SELLER')")
    @DeleteMapping("/{id}/image")
    public ResponseEntity<ProductResponseDto> deleteImageFromProduct(
            @RequestHeader(name = "${security.user-id-header}") String userId,
            @PathVariable UUID id,
            @RequestParam String imageName
    ) {
        UUID sellerId = UUID.fromString(userId);
        return ResponseEntity.ok(productService.deleteImageFromProduct(id, imageName, sellerId));
    }
}