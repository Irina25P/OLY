package com.example.product.service;

import com.example.product.dto.CreateReviewRequest;
import com.example.product.dto.ReviewDetailDTO;
import com.example.product.dto.SellerReplyDetailDTO;
import com.example.product.entity.Product;
import com.example.product.entity.Review;
import com.example.product.entity.SellerReply;
import com.example.product.entity.User;
import com.example.product.exception.ReviewException;
import com.example.product.repository.ProductRepository;
import com.example.product.repository.ReviewRepository;
import com.example.product.repository.SellerReplyRepository;
import com.example.product.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.product.rabbitmq.producer.ProductEventProducer;
import com.example.product.rabbitmq.dto.NotificationMessageDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final SellerReplyRepository sellerReplyRepository;
    private final ProductEventProducer productEventProducer;
    private static final int MIN_RATING = 1;
    private static final int MAX_RATING = 5;

    public ReviewDetailDTO createReview(CreateReviewRequest request, UUID userId) {
        if (request.getRating() < MIN_RATING || request.getRating() > MAX_RATING) {
            throw new ReviewException("Rating must be between " + MIN_RATING + " and " + MAX_RATING);
        }

        if (reviewRepository.findByProductIdAndUserId(request.getProductId(), userId).isPresent()) {
            throw new ReviewException("User has already reviewed this product");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ReviewException("User not found with id: " + userId));
        
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ReviewException("Product not found with id: " + request.getProductId()));

        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setCreatedAt(LocalDateTime.now());

        Review savedReview = reviewRepository.save(review);

        productEventProducer.sendReviewCreatedNotification(
                new NotificationMessageDto(
                        product.getSeller().getId(),
                        "A review has been created for product: " + product.getName()
                )
        );

        return ReviewDetailDTO.builder()
                .id(savedReview.getId())
                .clientId(savedReview.getUser().getId())
                .createdAt(savedReview.getCreatedAt())
                .comment(savedReview.getComment())
                .rating(savedReview.getRating())
                .build();
    }


    public List<ReviewDetailDTO> getReviewsByProductId(UUID productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId);

        List<ReviewDetailDTO> reviewDetails = new ArrayList<>();

        for (Review review : reviews) {
            SellerReply reply = sellerReplyRepository.findByReviewId(review.getId());

            SellerReplyDetailDTO replyDetailDTO = null;

            if (reply != null) {
                replyDetailDTO = SellerReplyDetailDTO.builder()
                        .id(reply.getId())
                        .sellerId(reply.getUser().getId())
                        .reply(reply.getReply())
                        .createdAt(reply.getCreatedAt()).build();
            }
            ReviewDetailDTO reviewDetailDTO = ReviewDetailDTO.builder()
                    .id(review.getId())
                    .clientId(review.getUser().getId())
                    .rating(review.getRating())
                    .createdAt(review.getCreatedAt())
                    .comment(review.getComment())
                    .sellerReply(replyDetailDTO).build();

            reviewDetails.add(reviewDetailDTO);
        }

        return reviewDetails;
    }

    public void deleteReview(UUID reviewId, UUID userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException("Review not found with id: " + reviewId));
        if (!review.getUser().getId().equals(userId)) {
            throw new ReviewException("User is not authorized to delete this review");
        }
        reviewRepository.deleteById(reviewId);
    }

    public Double getAverageRatingByProductId(UUID productId) {
        Double avg = reviewRepository.getAverageRatingByProductId(productId);
        return avg == null ? 0.0 : avg;
    }
}
