package com.example.order.dto.mapper;

import com.example.order.dto.ProductDto;
import com.example.order.entities.Product;

public class ProductMapper {

    public static Product toProduct(ProductDto productDto) {
        Product product = new Product();

        product.setId(productDto.getId());
        product.setStock(productDto.getStock());
        product.setPrice(productDto.getPrice());

        return product;
    }
}
