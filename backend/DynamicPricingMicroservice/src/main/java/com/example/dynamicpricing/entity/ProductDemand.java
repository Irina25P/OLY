package com.example.dynamicpricing.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "product_demand")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDemand {

    @Id
    private UUID productId;

    @Column(nullable = false)
    private String productName;

    private Double currentPrice;

    private Integer currentStock;

    private UUID sellerId;

    private String category;

    @Column(nullable = false)
    private Integer totalOrderCount = 0;

    private Double recommendedPrice;

    @Enumerated(EnumType.STRING)
    private PriceDirection priceDirection = PriceDirection.MAINTAIN;

    private Boolean applied = false;

    public enum PriceDirection {
        INCREASE, DECREASE, MAINTAIN
    }
}
