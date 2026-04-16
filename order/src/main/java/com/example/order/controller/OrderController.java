package com.example.order.controller;

import com.example.order.dto.OrderDto;
import com.example.order.model.Order;
import com.example.order.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping()
    public List<Order> findAll() {
        return orderService.findAll();
    }

    @GetMapping("/{orderId}")
    public Order findById(@PathVariable UUID orderId) {
        return orderService.findById(orderId);
    }

    @PostMapping()
    public Order save(@RequestBody OrderDto order) {
        return orderService.save(order);
    }
}
