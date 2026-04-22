package com.example.product.repository;

import com.example.product.entity.SellerReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SellerReplyRepository extends JpaRepository<SellerReply, UUID> {
    SellerReply findByReviewId(UUID reviewId);

    Optional<SellerReply> findByReviewIdAndUserId(UUID reviewId, UUID userId);
}
