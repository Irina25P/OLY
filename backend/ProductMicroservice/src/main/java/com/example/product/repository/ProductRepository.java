package com.example.product.repository;

import com.example.product.entity.InterestType;
import com.example.product.entity.Product;
import com.example.product.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findBySeller(User seller);

    void deleteBySeller(User seller);
}