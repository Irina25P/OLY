package com.example.order.service;

import com.example.order.dto.OrderDto;
import com.example.order.dto.convertor.OrderConvertor;
import com.example.order.model.Order;
import com.example.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> findAll()  {

        return orderRepository.findAll();
    }

    public Order findById(UUID orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    public Order save(OrderDto orderDto) {
        return orderRepository.save(OrderConvertor.toOrder(orderDto));
    }
}
