package com.example.order.dto;

import com.example.order.model.enums.OrderStatus;
import com.example.order.model.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private UUID id;

    @NotNull
    private UUID clientId;

    @NotNull
    private UUID sellerId;

    @NotNull
    private OrderStatus statusType;

    @NotNull
    private PaymentMethod payment;

    @NotNull
    private String ShippingDetails;

    private Double fullPrice;

    public OrderDto(UUID clientId, UUID sellerId, OrderStatus statusType, PaymentMethod payment, String ShippingDetails) {
        this.clientId = clientId;
        this.sellerId = sellerId;
        this.statusType = statusType;
        this.payment = payment;
        this.ShippingDetails = ShippingDetails;
    }
}
