package com.example.order.dto;

import com.example.order.model.enums.OrderStatus;
import com.example.order.model.enums.PaymentMethod;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
public class OrderDto {
    private UUID id;

    private UUID userId;

    private OrderStatus statusType;

    private PaymentMethod payment;

    public OrderDto( UUID userId, OrderStatus statusType, PaymentMethod payment) {
        this.userId = userId;
        this.statusType = statusType;
        this.payment = payment;
    }
}
