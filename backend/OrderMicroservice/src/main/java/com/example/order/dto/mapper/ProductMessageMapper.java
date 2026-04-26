package com.example.order.dto.mapper;

import com.example.order.dto.ProductMessageDto;
import com.example.order.entities.Product;

public class ProductMessageMapper {

    public static ProductMessageDto toMessage(Product product) {
        ProductMessageDto productMessageDto = new ProductMessageDto();
        productMessageDto.setId(product.getId());
        productMessageDto.setStock(product.getStock());

        return productMessageDto;
    }
}
