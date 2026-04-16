package com.example.order.dto.mapper;

import com.example.order.dto.OrderDto;
import com.example.order.model.Order;
import com.example.order.model.User;

public class OrderMapper {

    public static Order toOrder(OrderDto orderDto) {
        User user = null;
        if (orderDto.getUserId() != null) {
            user = new User();
            user.setId(orderDto.getUserId());
        }

        return new Order(
                orderDto.getId(),
                user,
                orderDto.getStatusType(),
                orderDto.getPayment()
        );

    }
}
