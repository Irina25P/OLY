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
    @JoinColumn(name = "user_id")
    private User userId;

    @Column(name = "status_type")
    private OrderStatus statusType;

    @Column(name = "payment")
    private PaymentMethod payment;
}
