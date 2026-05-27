package com.example.product.rabbitmq.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangedStockMessageDto {
    private UUID id;
    private Integer stock;
    private Double price;
    private String name;
    private String category;
    private UUID sellerId;
    private String firstImageName;

    public ChangedStockMessageDto(UUID id, Integer stock, Double price) {
        this.id = id;
        this.stock = stock;
        this.price = price;
    }
}
