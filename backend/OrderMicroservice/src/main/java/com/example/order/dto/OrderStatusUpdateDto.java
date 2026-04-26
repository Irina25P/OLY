package com.example.order.dto;

import com.example.order.entities.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateDto {
    @NotNull(message = "When updating a order status the id of the order is required")
    private UUID id;

    @NotNull(message = "The new status is required for updating a order status")
    private OrderStatus status;
}
