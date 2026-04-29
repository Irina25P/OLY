package com.example.order.repository;

import com.example.order.entities.Order;
import com.example.order.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID>{
    List<Order> findOrdersByClient(User user);

    List<Order> findOrdersBySeller(User user);

    void deleteByClient_Id(UUID clientId);

    void deleteBySeller_Id(UUID sellerId);
}
