package com.example.order.service;

import com.example.order.dto.ProductDto;
import com.example.order.dto.mapper.ProductMapper;
import com.example.order.entities.Product;
import com.example.order.repository.OrderProductsRepository;
import com.example.order.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final OrderProductsRepository orderProductsRepository;

    public ProductService(ProductRepository productRepository, OrderProductsRepository orderProductsRepository) {
        this.productRepository = productRepository;
        this.orderProductsRepository = orderProductsRepository;
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

    @Transactional
    public void delete(ProductDto productDto) {
        UUID productId = productDto.getId();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product with id " + productId + " not found"));

        // Delete all order products that reference this product
        orderProductsRepository.deleteByProduct_Id(productId);

        // Delete the product itself
        productRepository.delete(product);
    }
}
