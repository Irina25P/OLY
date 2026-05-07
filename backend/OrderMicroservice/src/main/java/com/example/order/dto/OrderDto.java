package com.example.order.dto;

import com.example.order.entities.enums.County;
import com.example.order.entities.enums.OrderStatus;
import com.example.order.entities.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private UUID id;

    private UUID clientId;

    @NotNull
    private UUID sellerId;

    @NotNull
    private OrderStatus statusType;

    @NotNull
    private PaymentMethod payment;

    @NotNull
    private County county;

    @NotNull
    private String city;

    @NotNull
    private String street;

    private Double fullPrice;

    private Timestamp createdAt;

    public OrderDto(UUID clientId, UUID sellerId, OrderStatus statusType, PaymentMethod payment, County county, String  city, String street, Timestamp createdAt) {
        this.clientId = clientId;
        this.sellerId = sellerId;
        this.statusType = statusType;
        this.payment = payment;
        this.county = county;
        this.city = city;
        this.street = street;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Your order " + id + " is " + statusType + " with the payment method " + payment
                + " is set to be delivered at " + county.name() + " " +  city + " " + street
                + ". As soon as we have an estimate from the delivery company we will notify you.";
    }
}
