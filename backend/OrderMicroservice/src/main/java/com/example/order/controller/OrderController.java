package com.example.order.controller;

import com.example.order.dto.OrderDto;
import com.example.order.dto.OrderFullDto;
import com.example.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/client")
    public ResponseEntity<List<OrderDto>> getByBuyerId(@RequestHeader(name = "${security.user-id-header}") String userId){
        List<OrderDto> orders = orderService.findByUserId(UUID.fromString(userId), true);
        return ResponseEntity.ok(orders);
    }

    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/seller")
    public ResponseEntity<List<OrderDto>> getBySellerId(@RequestHeader(name = "${security.user-id-header}") String userId){
        List<OrderDto> orders = orderService.findByUserId(UUID.fromString(userId), false);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderFullDto> getFullOrder(@PathVariable String orderId, @RequestHeader(name = "${security.user-id-header}") String userId){
        OrderFullDto fullOrder = orderService.findFullOrderById(UUID.fromString(orderId), UUID.fromString(userId));
        return ResponseEntity.ok(fullOrder);
    }

    @PostMapping()
    public ResponseEntity<String> save(@Valid @RequestBody OrderFullDto request) {
        return ResponseEntity.status(201).body(orderService.save(request.getOrder(), request.getOrderProducts()));
    }
}
