package dtos;

import constants.County;
import constants.OrderStatus;
import constants.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private UUID id;

    private UUID clientId;

    private UUID sellerId;

    private OrderStatus statusType;

    private PaymentMethod payment;

    private County county;

    private String city;

    private String street;

    public Double fullPrice;

    public OrderDto(UUID sellerId, OrderStatus statusType, PaymentMethod payment, County county, String  city, String street) {
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
                + " is set to be delivered at " + county.name() + " " +  city + " " + street
                + ". As soon as we have an estimate from the delivery company we will notify you.";
    }
}
