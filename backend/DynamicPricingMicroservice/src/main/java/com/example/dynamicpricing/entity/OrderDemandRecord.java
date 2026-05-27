package com.example.dynamicpricing.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "order_demand_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDemandRecord {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Timestamp orderedAt;
}
