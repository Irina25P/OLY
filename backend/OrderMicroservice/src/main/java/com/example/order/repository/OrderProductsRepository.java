package com.example.order.repository;

import com.example.order.entities.OrderProducts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderProductsRepository extends JpaRepository<OrderProducts, UUID> {
	List<OrderProducts> findAllByOrder_Id(UUID orderId);
}

