package com.example.product.service;

import com.example.product.dto.*;
import com.example.product.entity.*;
import com.example.product.entity.Product;
import com.example.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ReviewService reviewService;

    public List<ProductResponseDto> getAllProducts(
            String title,
            InterestType category,
            Double minPrice,
            Double maxPrice,
            Double minRating,
            Boolean available
    ) {
        List<Product> products = productRepository.findAll();

        return products.stream()
                .filter(product -> title == null || title.isBlank()
                        || product.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(product -> category == null
                        || product.getCategory() == category)
                .filter(product -> minPrice == null
                        || product.getPrice() >= minPrice)
                .filter(product -> maxPrice == null
                        || product.getPrice() <= maxPrice)
                .filter(product -> available == null
                        ? Boolean.TRUE.equals(product.getAvailable())
                        : product.getAvailable().equals(available))
                .filter(product -> minRating == null
                        || reviewService.getAverageRatingByProductId(product.getId()) >= minRating)
                .map(this::mapToResponseDto)
                .toList();
    }

    public ProductResponseDto getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return mapToResponseDto(product);
    }

    public ProductResponseDto createProduct(ProductRequestDto dto, UUID sellerId) {
        validateProduct(dto);

        Product product = new Product();
        product.setSellerId(sellerId);
        product.setTitle(dto.getTitle());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setCategory(dto.getCategory());
        product.setAvailable(true);
        product.setImageNames(dto.getImageNames());

        Product savedProduct = productRepository.save(product);
        return mapToResponseDto(savedProduct);
    }

    public ProductResponseDto updateProduct(UUID id, ProductRequestDto dto, UUID sellerId) {
        validateProduct(dto);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getSellerId().equals(sellerId)) {
            throw new RuntimeException("You are not allowed to update this product");
        }

        product.setSellerId(sellerId);
        product.setTitle(dto.getTitle());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setCategory(dto.getCategory());
        product.setAvailable(dto.getAvailable());
        product.setImageNames(dto.getImageNames());

        Product updatedProduct = productRepository.save(product);
        return mapToResponseDto(updatedProduct);
    }

    public void deleteProduct(UUID id, UUID sellerId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getSellerId().equals(sellerId)) {
            throw new RuntimeException("You are not allowed to delete this product");
        }

        productRepository.delete(product);
    }

    public ProductResponseDto updateStock(UUID id, Integer stock, UUID sellerId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (stock == null || stock < 0) {
            throw new RuntimeException("Stock must be 0 or greater");
        }

        if (!product.getSellerId().equals(sellerId)) {
            throw new RuntimeException("You are not allowed to update this product");
        }

        product.setStock(stock);

        Product updatedProduct = productRepository.save(product);
        return mapToResponseDto(updatedProduct);
    }

    public ProductResponseDto updateAvailability(UUID id, Boolean available, UUID sellerId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getSellerId().equals(sellerId)) {
            throw new RuntimeException("You are not allowed to update this product");
        }

        if (available == null) {
            throw new RuntimeException("Availability is required");
        }

        product.setAvailable(available);

        Product updatedProduct = productRepository.save(product);
        return mapToResponseDto(updatedProduct);
    }

    private void validateProduct(ProductRequestDto dto) {
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new RuntimeException("Title is required");
        }

        if (dto.getPrice() == null || dto.getPrice() <= 0) {
            throw new RuntimeException("Price must be greater than 0");
        }

        if (dto.getStock() == null || dto.getStock() < 0) {
            throw new RuntimeException("Stock must be 0 or greater");
        }

        if (dto.getImageNames() == null || dto.getImageNames().isEmpty()) {
            throw new RuntimeException("At least one image is required");
        }

        if (dto.getCategory() == null) {
            throw new RuntimeException("Category is required");
        }
    }

    private ProductResponseDto mapToResponseDto(Product product) {
        Double averageRating = reviewService.getAverageRatingByProductId(product.getId());
        List<String> imageUrls = product.getImageNames() == null ? List.of() : product.getImageNames();

        return new ProductResponseDto(
                product.getId(),
                product.getTitle(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                imageUrls,
                product.getCategory(),
                product.getAvailable(),
                averageRating
        );
    }
}