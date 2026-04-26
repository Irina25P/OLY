package com.example.order.dto;

import com.example.order.entities.Product;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductMessageDto {
    private UUID id;

    private Integer stock;
}
