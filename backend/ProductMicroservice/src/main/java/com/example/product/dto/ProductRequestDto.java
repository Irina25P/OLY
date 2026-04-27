package com.example.product.dto;

import com.example.product.entity.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDto {

    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private List<String> imageNames;
    private InterestType category;
}