package com.example.order.controller;

import com.example.order.dto.OrderDto;
import com.example.order.dto.OrderFullDto;
import com.example.order.dto.OrderStatusUpdateDto;
import com.example.order.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ResponseStatus;
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

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public String save(@Valid @RequestBody OrderFullDto request, @RequestHeader(name = "${security.user-id-header}") String userId) {
        request.getOrder().setClientId(UUID.fromString(userId));
        return orderService.save(request.getOrder(), request.getOrderProducts());
    }

    @PreAuthorize("hasRole('SELLER')")
    @PutMapping()
    public ResponseEntity<String> update(@Valid @RequestBody OrderStatusUpdateDto request, @RequestHeader(name = "${security.user-id-header}") String userId) {
        try{
           String result = orderService.updateOrderStatus(request, UUID.fromString(userId));

           return ResponseEntity.ok(result);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }

    }
}
