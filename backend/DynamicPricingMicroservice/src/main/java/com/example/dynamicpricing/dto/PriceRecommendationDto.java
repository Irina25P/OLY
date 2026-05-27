package com.example.dynamicpricing.dto;

import com.example.dynamicpricing.entity.ProductDemand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PriceRecommendationDto {
    private UUID productId;
    private String productName;
    private String category;
    private Double currentPrice;
    private Double recommendedPrice;
    private Integer currentStock;
    private Integer totalOrderCount;
    private Integer recentOrderCount7Days;
    private String priceDirection;
    private Boolean applied;
}
