package com.example.order.dto.mapper;

import com.example.order.dto.OrderDto;
import com.example.order.model.Order;
import com.example.order.model.User;

public class OrderMapper {

    public static Order toOrder(OrderDto orderDto) {
        User buyer = null;
        if (orderDto.getClientId() != null) {
            buyer = new User();
            buyer.setId(orderDto.getClientId());
        }

        User seller = null;
        if (orderDto.getSellerId() != null) {
            seller = new User();
            seller.setId(orderDto.getSellerId());
        }

        return new Order(
                orderDto.getId(),
                buyer,
                seller,
                orderDto.getStatusType(),
                orderDto.getPayment(),
                orderDto.getShippingDetails()
        );
    }

    public static OrderDto toOrderDto(Order order) {
        OrderDto dto = new OrderDto(
                order.getClient() != null ? order.getClient().getId() : null,
                order.getSeller() != null ? order.getSeller().getId() : null,
                order.getStatusType(),
                order.getPayment(),
                order.getShippingDetails()
        );
        dto.setId(order.getId());
        return dto;
    }

    public static OrderDto toOrderDto(Order order, Double fullPrice) {
        OrderDto dto = toOrderDto(order);
        dto.setFullPrice(fullPrice);
        return dto;
    }
}
