package com.example.product.repository;

import com.example.product.entity.Review;
import com.example.product.entity.SellerReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByProductId(UUID productId);

    Optional<Review> findByProductIdAndUserId(UUID productId, UUID userId);

    Optional<Review> findBySellerReply(SellerReply sellerReply);

    void deleteByUserId(UUID userId);

    void deleteByProductId(UUID productId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double getAverageRatingByProductId(@Param("productId") UUID productId);
}
