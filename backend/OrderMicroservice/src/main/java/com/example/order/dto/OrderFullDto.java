package com.example.order.dto;

import lombok.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderFullDto {

    @Valid
    @NotNull
    private OrderDto order;

    @Valid
    @NotNull
    private List<OrderProductDto> orderProducts;
}

