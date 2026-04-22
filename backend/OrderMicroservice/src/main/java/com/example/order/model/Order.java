package com.example.order.model;


import com.example.order.model.enums.OrderStatus;
import com.example.order.model.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private User client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller;

    @Column(name = "status_type")
    @Enumerated(EnumType.STRING)
    private OrderStatus statusType;

    @Column(name = "payment")
    @Enumerated(EnumType.STRING)
    private PaymentMethod payment;

    @Column(name = "shipping_details")
    private String shippingDetails;
}
