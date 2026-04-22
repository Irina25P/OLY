package com.example.order.dto.mapper;

import com.example.order.dto.OrderProductDto;
import com.example.order.model.Order;
import com.example.order.model.OrderProducts;
import com.example.order.model.Product;

public class OrderProductsMapper {
    public static OrderProducts toOrderProducts(OrderProductDto dto)
    {
        Order order = null;
        if (dto.getOrderId() != null) {
            order = new Order();
            order.setId(dto.getOrderId());
        }

        Product product = null;
        if (dto.getProductId() != null) {
            product = new Product();
            product.setId(dto.getProductId());
        }

        return new OrderProducts(
                dto.getId(),
                order,
                product,
                dto.getQuantity()
        );
    }

    public static OrderProductDto toOrderProductDto(OrderProducts entity) {
        OrderProductDto dto = new OrderProductDto(
                entity.getId(),
                entity.getOrder() != null ? entity.getOrder().getId() : null,
                entity.getProduct() != null ? entity.getProduct().getId() : null,
                entity.getQuantity()
        );

        if (entity.getProduct() != null && entity.getProduct().getPrice() != null) {
            dto.setPricePerProduct(entity.getProduct().getPrice());
        }

        return dto;
    }
}
