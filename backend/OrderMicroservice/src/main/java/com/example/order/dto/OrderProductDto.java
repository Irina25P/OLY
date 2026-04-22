package com.example.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductDto {
    private UUID id;

    private UUID orderId;

    @NotNull
    private UUID productId;

    @NotNull
    private Integer quantity;

    private Double pricePerProduct;

    public OrderProductDto(UUID id, UUID orderId, UUID productId, Integer quantity) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
    }
}
