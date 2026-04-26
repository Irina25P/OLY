package com.example.order.service;

import com.example.order.dto.*;
import com.example.order.dto.mapper.OrderMapper;
import com.example.order.dto.mapper.OrderNotificationMapper;
import com.example.order.dto.mapper.OrderProductsMapper;
import com.example.order.dto.mapper.ProductMessageMapper;
import com.example.order.entities.Order;
import com.example.order.entities.Product;
import com.example.order.entities.User;
import com.example.order.entities.enums.OrderStatus;
import com.example.order.repository.OrderRepository;
import com.example.order.repository.OrderProductsRepository;
import com.example.order.repository.ProductRepository;
import com.example.order.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.util.*;

@Service
public class OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final OrderProductsRepository orderProductsRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${rabbitmq.sync.exchange.changeStock.topic}")
    private String syncExchangeChangeStockTopic;

    @Value("${rabbitmq.sync.exchange.notification.topic}")
    private String syncExchangeNotificationTopic;

    @Value("${rabbitmq.sync.routingkey.changeStock}")
    private String routingKeyChangeStock;

    @Value("${rabbitmq.sync.routingkey.orderCreated}")
    private String routingKeyOrderCreated;

    @Value("${rabbitmq.sync.routingkey.updateOrder}")
    private String routingKeyUpdateOrder;

    public OrderService(
            OrderRepository orderRepository,
            OrderProductsRepository orderProductsRepository,
            UserRepository userRepository,
            ProductRepository productRepository,
            RabbitTemplate rabbitTemplate,
            ObjectMapper objectMapper
    ) {
        this.orderRepository = orderRepository;
        this.orderProductsRepository = orderProductsRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
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
                    double price = op.getProduct() != null ? op.getProduct().getPrice() : 0.0;
                    return price * op.getQuantity();
                })
                .sum();
    }

    @Transactional
    public String save(OrderDto orderDto, List<OrderProductDto> orderProducts) {
        validateStock(orderProducts);

        Order order = orderRepository.save(OrderMapper.toOrder(orderDto));
        orderProducts.forEach(orderProductDto -> {
            orderProductDto.setOrderId(order.getId());
            orderProductsRepository.save(OrderProductsMapper.toOrderProducts(orderProductDto));
        });

        try{
            String orderMessage = objectMapper.writeValueAsString(OrderNotificationMapper.toOrderNotificationDto(orderDto));
            rabbitTemplate.convertAndSend(syncExchangeNotificationTopic, routingKeyOrderCreated, orderMessage);
            LOGGER.info("Sent order created message for order id {}: {}", order.getId(), orderMessage);
        } catch (Exception e){
            LOGGER.error("Error while sending order created message for order id {}", order.getId(), e);
        }

        return "Your order has been placed successfully!";
    }

    private void validateStock(List<OrderProductDto> orderProducts) {
        if (orderProducts == null || orderProducts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order must contain at least one product");
        }

        Map<UUID, Integer> requestedQuantities = new HashMap<>();
        for (OrderProductDto orderProductDto : orderProducts) {
            if (orderProductDto.getProductId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Each ordered product must have a product id");
            }

            if (orderProductDto.getQuantity() == null || orderProductDto.getQuantity() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be greater than 0");
            }

            requestedQuantities.merge(orderProductDto.getProductId(), orderProductDto.getQuantity(), Integer::sum);
        }

        for (Map.Entry<UUID, Integer> entry : requestedQuantities.entrySet()) {
            UUID productId = entry.getKey();
            int requestedQuantity = entry.getValue();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with id " + productId + " not found"));

            int availableStock = product.getStock() != null ? product.getStock() : 0;
            if (requestedQuantity > availableStock) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Not enough stock for product with id " + productId + ". Requested " + requestedQuantity + ", available " + availableStock);
            } else {
                product.setStock(availableStock - requestedQuantity);
                productRepository.save(product);

                try{
                    ProductMessageDto productMessageDto = ProductMessageMapper.toMessage(product);
                    String message = objectMapper.writeValueAsString(productMessageDto);
                    rabbitTemplate.convertAndSend(syncExchangeChangeStockTopic, routingKeyChangeStock, message);
                    LOGGER.info("Sent change stock message for product id {}: {}", productId, message);
                } catch (Exception e){
                    LOGGER.error("Error while sending change stock message for product id {}", productId, e);
                }
            }
        }
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

    public String updateOrderStatus(OrderStatusUpdateDto orderDto, UUID userId) {
        Order order =  orderRepository.findById(orderDto.getId()).orElseThrow(() -> new EntityNotFoundException("Order not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!order.getSeller().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not allowed to access this order");
        }

        try{
            String message = objectMapper.writeValueAsString(OrderNotificationMapper.toOrderNotificationDto(OrderMapper.toOrderDto(order)));
            rabbitTemplate.convertAndSend(syncExchangeNotificationTopic, routingKeyUpdateOrder, message);
            LOGGER.info("Sent update order message for order id {}: {}", order.getId(), message);
        } catch (Exception e){
            LOGGER.error("Error while sending order status update for order id {}", order.getId(), e);
        }

        return "Order has been updated successfully!";
    }
}
