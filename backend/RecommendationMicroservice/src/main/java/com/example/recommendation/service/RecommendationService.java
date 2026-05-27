package com.example.recommendation.service;

import com.example.recommendation.dto.ProductRecommendationDto;
import com.example.recommendation.node.ProductNode;
import com.example.recommendation.node.UserNode;
import com.example.recommendation.rabbitmq.messages.OrderEventMessage;
import com.example.recommendation.rabbitmq.messages.ProductMessage;
import com.example.recommendation.repository.ProductNodeRepository;
import com.example.recommendation.repository.UserNodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    private final ProductNodeRepository productNodeRepository;
    private final UserNodeRepository userNodeRepository;

    public List<ProductRecommendationDto> getRecommendations(String userId) {
        List<ProductNode> recommendations = productNodeRepository.findCollaborativeRecommendations(userId);

        if (recommendations.size() < 5) {
            List<ProductNode> categoryBased = productNodeRepository.findCategoryBasedRecommendations(userId);
            for (ProductNode p : categoryBased) {
                if (recommendations.stream().noneMatch(r -> r.getId().equals(p.getId()))) {
                    recommendations.add(p);
                }
                if (recommendations.size() >= 20) break;
            }
        }

        if (recommendations.isEmpty()) {
            recommendations = productNodeRepository.findMostPurchasedProducts();
        }

        return recommendations.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void recordPurchase(String clientId, String productId) {
        UserNode user = userNodeRepository.findById(clientId)
                .orElseGet(() -> userNodeRepository.save(new UserNode(clientId)));

        ProductNode product = productNodeRepository.findById(productId)
                .orElseGet(() -> productNodeRepository.save(new ProductNode(productId)));

        boolean alreadyPurchased = user.getPurchasedProducts().stream()
                .anyMatch(p -> p.getId().equals(productId));

        if (!alreadyPurchased) {
            user.getPurchasedProducts().add(product);
            userNodeRepository.save(user);
        }
    }

    @Transactional
    public void recordView(String userId, String productId) {
        UserNode user = userNodeRepository.findById(userId)
                .orElseGet(() -> userNodeRepository.save(new UserNode(userId)));

        ProductNode product = productNodeRepository.findById(productId)
                .orElseGet(() -> productNodeRepository.save(new ProductNode(productId)));

        boolean alreadyViewed = user.getViewedProducts().stream()
                .anyMatch(p -> p.getId().equals(productId));

        if (!alreadyViewed) {
            user.getViewedProducts().add(product);
            userNodeRepository.save(user);
        }
    }

    @Transactional
    public void processOrderEvent(OrderEventMessage event) {
        if (event.getClientId() == null || event.getProducts() == null) return;

        String clientId = event.getClientId().toString();
        event.getProducts().forEach(item -> {
            if (item.getProductId() != null) {
                recordPurchase(clientId, item.getProductId().toString());
            }
        });
    }

    @Transactional
    public void upsertProduct(ProductMessage msg) {
        if (msg.getId() == null) return;

        ProductNode product = productNodeRepository.findById(msg.getId().toString())
                .orElse(new ProductNode(msg.getId().toString()));

        if (msg.getName() != null) product.setName(msg.getName());
        if (msg.getPrice() != null) product.setPrice(msg.getPrice());
        if (msg.getCategory() != null) product.setCategory(msg.getCategory());
        if (msg.getSellerId() != null) product.setSellerId(msg.getSellerId().toString());
        if (msg.getFirstImageName() != null) product.setFirstImageName(msg.getFirstImageName());

        productNodeRepository.save(product);
    }

    @Transactional
    public void deleteProduct(String productId) {
        productNodeRepository.deleteById(productId);
    }

    private ProductRecommendationDto toDto(ProductNode node) {
        return new ProductRecommendationDto(
                node.getId(),
                node.getName(),
                node.getPrice(),
                node.getCategory(),
                node.getSellerId(),
                node.getFirstImageName()
        );
    }
}
