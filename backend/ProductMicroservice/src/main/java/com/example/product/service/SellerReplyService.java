package com.example.product.service;

import com.example.product.dto.CreateSellerReplyRequest;
import com.example.product.dto.SellerReplyDetailDTO;
import com.example.product.entity.Review;
import com.example.product.entity.SellerReply;
import com.example.product.entity.User;
import com.example.product.exception.ReviewException;
import com.example.product.exception.SellerReplyException;
import com.example.product.repository.ReviewRepository;
import com.example.product.repository.SellerReplyRepository;
import com.example.product.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class SellerReplyService {
    private final SellerReplyRepository sellerReplyRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public SellerReplyDetailDTO createReply(CreateSellerReplyRequest request, UUID userId) {
        if (sellerReplyRepository.findByReviewIdAndUserId(request.getReviewId(), userId).isPresent()) {
            throw new SellerReplyException("User has already replied to this product");
        }

        Review review = reviewRepository.findById(request.getReviewId())
                .orElseThrow(() -> new ReviewException("Review not found with id: " + request.getReviewId()));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ReviewException("User not found with id: " + userId));

        if (!review.getProduct().getSeller().getId().equals(userId)) {
            throw new SellerReplyException("User is not the seller of this product");
        }

        SellerReply sellerReply = new SellerReply();
        sellerReply.setReview(review);
        sellerReply.setUser(user);
        sellerReply.setReply(request.getReply());
        sellerReply.setCreatedAt(LocalDateTime.now());

        SellerReply savedReply = sellerReplyRepository.save(sellerReply);
        return SellerReplyDetailDTO.builder()
                .id(savedReply.getId())
                .reply(savedReply.getReply())
                .sellerId(savedReply.getUser().getId())
                .createdAt(savedReply.getCreatedAt())
                .build();
    }

    @Transactional
    public void deleteReply(UUID replyId, UUID userId) {
        SellerReply reply = sellerReplyRepository.findById(replyId)
                .orElseThrow(() -> new SellerReplyException("Reply not found with id: " + replyId));

        Review review = reviewRepository.findBySellerReply(reply)
                .orElseThrow(() -> new SellerReplyException("Review not found for this reply"));

        review.setSellerReply(null);
        reviewRepository.save(review);

        if (!reply.getUser().getId().equals(userId)) {
            System.out.println("S-a ajung aici !!!!!!");
            throw new SellerReplyException("User is not authorized to delete this reply");
        }
        sellerReplyRepository.deleteById(replyId);
    }
}


