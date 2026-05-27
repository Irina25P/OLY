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
public class ProductMessage {
    private UUID id;
    private Integer stock;
    private Double price;
    private String name;
    private String category;
    private UUID sellerId;
    private String firstImageName;
}
