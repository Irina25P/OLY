package com.example.dynamicpricing.service;

import com.example.dynamicpricing.dto.PriceRecommendationDto;
import com.example.dynamicpricing.entity.OrderDemandRecord;
import com.example.dynamicpricing.entity.ProductDemand;
import com.example.dynamicpricing.rabbitmq.PriceUpdateProducer;
import com.example.dynamicpricing.rabbitmq.messages.OrderEventMessage;
import com.example.dynamicpricing.rabbitmq.messages.ProductMessage;
import com.example.dynamicpricing.repository.OrderDemandRepository;
import com.example.dynamicpricing.repository.ProductDemandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DynamicPricingService {

    private final ProductDemandRepository productDemandRepository;
    private final OrderDemandRepository orderDemandRepository;
    private final PriceUpdateProducer priceUpdateProducer;

    public List<PriceRecommendationDto> getRecommendationsForSeller(UUID sellerId) {
        return productDemandRepository.findBySellerId(sellerId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public PriceRecommendationDto getRecommendationForProduct(UUID productId, UUID sellerId) {
        ProductDemand demand = productDemandRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No pricing data found for product " + productId));

        if (!sellerId.equals(demand.getSellerId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not the seller of this product");
        }

        return toDto(demand);
    }

    @Transactional
    public PriceRecommendationDto applyRecommendation(UUID productId, UUID sellerId) {
        ProductDemand demand = productDemandRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No pricing data found for product " + productId));

        if (!sellerId.equals(demand.getSellerId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not the seller of this product");
        }

        demand.setApplied(true);
        demand.setCurrentPrice(demand.getRecommendedPrice());
        productDemandRepository.save(demand);

        priceUpdateProducer.sendPriceUpdate(productId, demand.getRecommendedPrice());

        return toDto(demand);
    }

    @Transactional
    public void processOrderEvent(OrderEventMessage event) {
        if (event.getProducts() == null || event.getOrderId() == null) return;

        Timestamp orderedAt = event.getCreatedAt() != null
                ? event.getCreatedAt()
                : Timestamp.from(Instant.now());

        event.getProducts().forEach(item -> {
            if (item.getProductId() == null) return;

            OrderDemandRecord record = new OrderDemandRecord(
                    null,
                    item.getProductId(),
                    event.getOrderId(),
                    item.getQuantity() != null ? item.getQuantity() : 1,
                    orderedAt
            );
            orderDemandRepository.save(record);

            productDemandRepository.findById(item.getProductId()).ifPresent(demand -> {
                demand.setTotalOrderCount(demand.getTotalOrderCount() + (item.getQuantity() != null ? item.getQuantity() : 1));
                recalculateRecommendation(demand);
                productDemandRepository.save(demand);
            });
        });
    }

    @Transactional
    public void upsertProduct(ProductMessage msg) {
        if (msg.getId() == null) return;

        ProductDemand demand = productDemandRepository.findById(msg.getId())
                .orElse(new ProductDemand());

        demand.setProductId(msg.getId());
        if (msg.getName() != null) demand.setProductName(msg.getName());
        if (msg.getPrice() != null) demand.setCurrentPrice(msg.getPrice());
        if (msg.getStock() != null) demand.setCurrentStock(msg.getStock());
        if (msg.getSellerId() != null) demand.setSellerId(msg.getSellerId());
        if (msg.getCategory() != null) demand.setCategory(msg.getCategory());

        if (demand.getTotalOrderCount() == null) demand.setTotalOrderCount(0);
        if (demand.getApplied() == null) demand.setApplied(false);

        recalculateRecommendation(demand);
        productDemandRepository.save(demand);
    }

    @Transactional
    public void deleteProduct(UUID productId) {
        productDemandRepository.deleteById(productId);
        orderDemandRepository.findByProductIdAndOrderedAtAfter(
                productId,
                Timestamp.from(Instant.EPOCH)
        ).forEach(orderDemandRepository::delete);
    }

    private void recalculateRecommendation(ProductDemand demand) {
        Timestamp sevenDaysAgo = Timestamp.from(Instant.now().minus(7, ChronoUnit.DAYS));
        Timestamp thirtyDaysAgo = Timestamp.from(Instant.now().minus(30, ChronoUnit.DAYS));

        int count7Days = orderDemandRepository.countByProductIdAndOrderedAtAfter(demand.getProductId(), sevenDaysAgo);
        int count30Days = orderDemandRepository.countByProductIdAndOrderedAtAfter(demand.getProductId(), thirtyDaysAgo);

        double weeklyAvg = count30Days / 4.0;
        int stock = demand.getCurrentStock() != null ? demand.getCurrentStock() : 0;
        double currentPrice = demand.getCurrentPrice() != null ? demand.getCurrentPrice() : 0.0;

        ProductDemand.PriceDirection direction;
        double factor;

        if (count7Days > weeklyAvg * 1.5 && stock < 20) {
            direction = ProductDemand.PriceDirection.INCREASE;
            double ratio = weeklyAvg > 0 ? Math.min(count7Days / weeklyAvg, 3.0) : 1.5;
            factor = 1.0 + Math.min(0.20, 0.05 * ratio);
        } else if (count30Days > 0 && count7Days < weeklyAvg * 0.5 && stock > 50) {
            direction = ProductDemand.PriceDirection.DECREASE;
            factor = 0.90;
        } else {
            direction = ProductDemand.PriceDirection.MAINTAIN;
            factor = 1.0;
        }

        demand.setPriceDirection(direction);
        demand.setRecommendedPrice(Math.round(currentPrice * factor * 100.0) / 100.0);
        demand.setApplied(false);
    }

    private PriceRecommendationDto toDto(ProductDemand demand) {
        Timestamp sevenDaysAgo = Timestamp.from(Instant.now().minus(7, ChronoUnit.DAYS));
        int recentCount = orderDemandRepository.countByProductIdAndOrderedAtAfter(demand.getProductId(), sevenDaysAgo);

        return new PriceRecommendationDto(
                demand.getProductId(),
                demand.getProductName(),
                demand.getCategory(),
                demand.getCurrentPrice(),
                demand.getRecommendedPrice(),
                demand.getCurrentStock(),
                demand.getTotalOrderCount(),
                recentCount,
                demand.getPriceDirection() != null ? demand.getPriceDirection().name() : "MAINTAIN",
                demand.getApplied()
        );
    }
}
