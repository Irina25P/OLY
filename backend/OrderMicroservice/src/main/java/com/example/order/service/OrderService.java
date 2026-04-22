package com.example.order.service;

import com.example.order.dto.OrderDto;
import com.example.order.dto.OrderFullDto;
import com.example.order.dto.OrderProductDto;
import com.example.order.dto.mapper.OrderMapper;
import com.example.order.dto.mapper.OrderProductsMapper;
import com.example.order.model.Order;
import com.example.order.model.User;
import com.example.order.repository.OrderRepository;
import com.example.order.repository.OrderProductsRepository;
import com.example.order.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProductsRepository orderProductsRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, OrderProductsRepository orderProductsRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderProductsRepository = orderProductsRepository;
        this.userRepository = userRepository;
    }

    public List<OrderDto> findByUserId(UUID userId, boolean isClient){
        List<Order> orders = isClient
                ? orderRepository.findOrdersByClient(new User(userId))
                : orderRepository.findOrdersBySeller(new User(userId));

        return orders.stream()
                .filter(Objects::nonNull)
                .map(order -> {
                    Double fullPrice = calculateOrderFullPrice(order.getId());
                    return OrderMapper.toOrderDto(order, fullPrice);
                })
                .toList();
    }

    private Double calculateOrderFullPrice(UUID orderId) {
        return orderProductsRepository.findAllByOrder_Id(orderId)
                .stream()
                .mapToDouble(op -> {
                    Double price = op.getProduct() != null ? op.getProduct().getPrice() : 0.0;
                    return price * op.getQuantity();
                })
                .sum();
    }

    @Transactional
    public String save(OrderDto orderDto, List<OrderProductDto> orderProducts) {
        Order order = orderRepository.save(OrderMapper.toOrder(orderDto));
        orderProducts.forEach(orderProductDto -> {
            orderProductDto.setOrderId(order.getId());
            orderProductsRepository.save(OrderProductsMapper.toOrderProducts(orderProductDto));
        });
        return "Your order has been placed successfully!";
    }

    public OrderFullDto findFullOrderById(UUID orderId, UUID userId) {
        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        UUID clientId = order.getClient() != null ? order.getClient().getId() : null;
        UUID sellerId = order.getSeller() != null ? order.getSeller().getId() : null;
        boolean belongsToUser = userId.equals(clientId) || userId.equals(sellerId);
        if (!belongsToUser) {
            throw new AccessDeniedException("You are not allowed to access this order");
        }

        List<OrderProductDto> orderProducts = orderProductsRepository.findAllByOrder_Id(orderId)
                .stream()
                .map(OrderProductsMapper::toOrderProductDto)
                .toList();

        Double fullPrice = orderProducts.stream()
                .mapToDouble(op -> {
                    Double price = op.getPricePerProduct() != null ? op.getPricePerProduct() : 0.0;
                    return price * op.getQuantity();
                })
                .sum();

        return new OrderFullDto(OrderMapper.toOrderDto(order, fullPrice), orderProducts);
    }
}
