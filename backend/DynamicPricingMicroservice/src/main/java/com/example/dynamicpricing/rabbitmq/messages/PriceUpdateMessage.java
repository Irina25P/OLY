package com.example.dynamicpricing.rabbitmq.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PriceUpdateMessage {
    private UUID productId;
    private Double newPrice;
}
