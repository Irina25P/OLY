package com.example.order.dto.mapper;

import com.example.order.dto.OrderDto;
import com.example.order.dto.OrderNotificationDto;

public class OrderNotificationMapper {
    public static OrderNotificationDto toOrderNotificationDto(OrderDto orderDto) {
        OrderNotificationDto orderNotificationDto = new OrderNotificationDto();

        orderNotificationDto.setMessage(orderDto.toString());
        orderNotificationDto.setReceiverId(orderDto.getClientId());

        return orderNotificationDto;
    }
}
