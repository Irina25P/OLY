package com.example.recommendation.rabbitmq.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderEventMessage {
    private UUID orderId;
    private UUID clientId;
    private UUID sellerId;
    private Timestamp createdAt;
    private List<OrderEventProductItem> products;
}
