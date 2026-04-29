package factories;

import constants.County;
import constants.OrderStatus;
import dtos.*;
import net.datafaker.Faker;
import org.junit.jupiter.api.Order;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static constants.PaymentMethod.CARD;

public class OrderFactory {
    private static final Faker faker = new Faker();
    private static final Random random = new Random();

    public static OrderStatusUpdateDto createOrderStatus(UUID orderId) {
        return new OrderStatusUpdateDto(
                orderId,
                getRandomStatus()
        );
    }

    public static OrderFullDto createOrderFullDto(UUID sellerId, List<OrderProductDto> orderProducts) {
        OrderDto orderDto = new OrderDto(
                sellerId,
                OrderStatus.PLACED,
                CARD, // Payment method can be set to null or a random value if needed
                County.values()[random.nextInt(County.values().length)], // Random county
                faker.address().city(),
                faker.address().streetAddress()
        );
        return new OrderFullDto(orderDto, orderProducts);
    }

    public static OrderProductDto createOrderProductDto(ProductResponseDto productResponseDto) {
        return new OrderProductDto(productResponseDto.getId(), faker.number().numberBetween(1, productResponseDto.getStock()));
    }

    private static OrderStatus getRandomStatus() {
        OrderStatus[] orderStatuses = OrderStatus.values();
        OrderStatus randomOrderStatus = orderStatuses[random.nextInt(orderStatuses.length)];
        return randomOrderStatus;
    }
}
