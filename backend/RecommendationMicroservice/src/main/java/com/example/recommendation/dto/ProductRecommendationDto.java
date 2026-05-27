package com.example.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRecommendationDto {
    private String id;
    private String name;
    private Double price;
    private String category;
    private String sellerId;
    private String firstImageName;
}
