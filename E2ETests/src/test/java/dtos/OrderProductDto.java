package dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductDto {
    private UUID id;

    private UUID orderId;

    private UUID productId;

    private Integer quantity;

    private Double pricePerProduct;

    public OrderProductDto(UUID productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
