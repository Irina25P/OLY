package com.example.dynamicpricing.repository;

import com.example.dynamicpricing.entity.ProductDemand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductDemandRepository extends JpaRepository<ProductDemand, UUID> {
    List<ProductDemand> findBySellerId(UUID sellerId);
}
