package com.example.order.dto;

import com.example.order.entities.enums.OrderStatus;
import com.example.order.entities.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private UUID id;

    @NotNull
    private UUID clientId;

    @NotNull
    private UUID sellerId;

    @NotNull
    private OrderStatus statusType;

    @NotNull
    private PaymentMethod payment;

    @NotNull
    private String county;

    @NotNull
    private String city;

    @NotNull
    private String street;

    private Double fullPrice;

    public OrderDto(UUID clientId, UUID sellerId, OrderStatus statusType, PaymentMethod payment, String county, String  city, String street) {
        this.clientId = clientId;
        this.sellerId = sellerId;
        this.statusType = statusType;
        this.payment = payment;
        this.county = county;
        this.city = city;
        this.street = street;
    }

    @Override
    public String toString() {
        return "Your order " + id + " is " + statusType + " with the payment method " + payment
                + " is set to be delivered at " + county + " " +  city + " " + street
                + ". As soon as we have an estimate from the delivery company we will notify you.";
    }
}
