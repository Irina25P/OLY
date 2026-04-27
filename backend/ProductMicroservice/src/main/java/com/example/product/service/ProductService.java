package com.example.product.service;
import com.example.product.rabbitmq.dto.ChangedStockMessageDto;
import com.example.product.rabbitmq.dto.DeleteImageMessageDto;
import com.example.product.rabbitmq.producer.ProductEventProducer;
import com.example.product.dto.*;
import com.example.product.entity.*;
import com.example.product.entity.Product;
import com.example.product.rabbitmq.dto.OrderStockUpdateMessageDto;
import com.example.product.repository.ProductRepository;
import com.example.product.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ReviewService reviewService;
    private final ProductEventProducer productEventProducer;
    private final UserRepository userRepository;

    public List<ProductResponseDto> getAllProducts(
            String name,
            List<InterestType> categories,
            Double minPrice,
            Double maxPrice,
            Double minRating
    ) {
        List<Product> products = productRepository.findAll();

        return products.stream()
                .filter(product -> name == null || name.isBlank()
                        || product.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(product -> categories == null
                        || categories.contains(product.getCategory()))
                .filter(product -> minPrice == null
                        || product.getPrice() >= minPrice)
                .filter(product -> maxPrice == null
                        || product.getPrice() <= maxPrice)
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

        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        Product product = new Product();
        seller.setId(sellerId);
        product.setSeller(seller);
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setCategory(dto.getCategory());
        product.setImageNames(dto.getImageNames());

        Product savedProduct = productRepository.save(product);
        productEventProducer.sendProductCreated(
                new ChangedStockMessageDto(
                        savedProduct.getId(),
                        savedProduct.getStock(),
                        savedProduct.getPrice()
                )
        );

        return mapToResponseDto(savedProduct);
    }

    public ProductResponseDto updateProduct(UUID id, ProductRequestDto dto, UUID sellerId) {
        validateProduct(dto);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getSeller().getId().equals(sellerId)) {
            throw new RuntimeException("You are not allowed to update this product");
        }

        List<String> oldImages = product.getImageNames();

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setCategory(dto.getCategory());
        product.setImageNames(dto.getImageNames());

        Product updatedProduct = productRepository.save(product);
        productEventProducer.sendProductUpdated(
                new ChangedStockMessageDto(
                        updatedProduct.getId(),
                        updatedProduct.getStock(),
                        updatedProduct.getPrice()
                )
        );

        List<String> imagesToBeDeleted = new ArrayList<>();
        for (String oldImage : oldImages) {
            if (!product.getImageNames().contains(oldImage)) {
                imagesToBeDeleted.add(oldImage);
            }
        }

        DeleteImageMessageDto deleteImageMessageDto = new DeleteImageMessageDto(imagesToBeDeleted);
        productEventProducer.sendImageDeleted(deleteImageMessageDto);

        return mapToResponseDto(updatedProduct);
    }

    public void deleteProduct(UUID id, UUID sellerId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getSeller().getId().equals(sellerId)) {
            throw new RuntimeException("You are not allowed to delete this product");
        }

        productEventProducer.sendImageDeleted(new DeleteImageMessageDto(product.getImageNames()));

        productEventProducer.sendProductDeleted(
                new ChangedStockMessageDto(
                        product.getId(),
                        product.getStock(),
                        product.getPrice()
                )
        );


        productRepository.delete(product);
    }

    public ProductResponseDto updateStock(UUID id, Integer stock, UUID sellerId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (stock == null || stock < 0) {
            throw new RuntimeException("Stock must be 0 or greater");
        }

        if (!product.getSeller().getId().equals(sellerId)) {
            throw new RuntimeException("You are not allowed to update this product");
        }

        product.setStock(stock);

        Product updatedProduct = productRepository.save(product);

        productEventProducer.sendProductUpdated(
                new ChangedStockMessageDto(
                        updatedProduct.getId(),
                        updatedProduct.getStock(),
                        updatedProduct.getPrice()
                )
        );
        return mapToResponseDto(updatedProduct);
    }

    public ProductResponseDto deleteImageFromProduct(UUID productId, String imageName, UUID sellerId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getSeller().getId().equals(sellerId)) {
            throw new RuntimeException("You are not allowed to update this product");
        }

        if (imageName == null || imageName.isBlank()) {
            throw new RuntimeException("Image name is required");
        }

        if (product.getImageNames() == null || !product.getImageNames().remove(imageName)) {
            throw new RuntimeException("Image not found for this product");
        }

        Product updatedProduct = productRepository.save(product);

        productEventProducer.sendImageDeleted(
                new com.example.product.rabbitmq.dto.DeleteImageMessageDto(List.of(imageName))
        );

        return mapToResponseDto(updatedProduct);
    }

    private void validateProduct(ProductRequestDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new RuntimeException("name is required");
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
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                imageUrls,
                product.getCategory(),
                averageRating
        );
    }

    public void updateStockFromOrder(OrderStockUpdateMessageDto dto) {
        if (dto.getProductId() == null) {
            throw new RuntimeException("Product id is required");
        }

        if (dto.getStock() == null || dto.getStock() < 0) {
            throw new RuntimeException("Stock must be 0 or greater");
        }

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setStock(dto.getStock());

        productRepository.save(product);
    }
}