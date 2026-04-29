package com.example.order.dto.mapper;

import com.example.order.dto.OrderDto;
import com.example.order.entities.Order;
import com.example.order.entities.User;

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
                orderDto.getCounty(),
                orderDto.getCity(),
                orderDto.getStreet()
        );
    }

    public static Order toOrder(OrderDto orderDto, User client, User seller) {
        return new Order(
                orderDto.getId(),
                client,
                seller,
                orderDto.getStatusType(),
                orderDto.getPayment(),
                orderDto.getCounty(),
                orderDto.getCity(),
                orderDto.getStreet()
        );
    }

    public static OrderDto toOrderDto(Order order) {
        OrderDto dto = new OrderDto(
                order.getClient() != null ? order.getClient().getId() : null,
                order.getSeller() != null ? order.getSeller().getId() : null,
                order.getStatusType(),
                order.getPayment(),
                order.getCounty(),
                order.getCity(),
                order.getStreet()
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
