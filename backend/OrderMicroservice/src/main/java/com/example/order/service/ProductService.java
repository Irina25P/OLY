package com.example.order.service;

import com.example.order.dto.ProductDto;
import com.example.order.dto.mapper.ProductMapper;
import com.example.order.entities.Product;
import com.example.order.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void save(ProductDto productDto) {
        Product product = ProductMapper.toProduct(productDto);

        productRepository.save(product);
    }

    public void update(ProductDto productDto) {
        Product product = productRepository.findById(productDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Product with id " + productDto.getId() + " not found"));

        productRepository.save(product);
    }

    public void delete(ProductDto productDto) {
        Product product = productRepository.findById(productDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Product with id " + productDto.getId() + " not found"));

        productRepository.delete(product);
    }
}
