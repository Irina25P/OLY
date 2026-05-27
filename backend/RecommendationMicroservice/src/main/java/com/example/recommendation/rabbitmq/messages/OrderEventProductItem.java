package com.example.recommendation.rabbitmq.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderEventProductItem {
    private UUID id;
    private UUID orderId;
    private UUID productId;
    private Integer quantity;
    private Double pricePerProduct;
}
