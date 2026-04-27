package com.example.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @UuidGenerator
	private UUID id;

    @Column(nullable=false)
	private Integer rating;

	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

	private String comment;

    @Column(nullable=false)
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "review", cascade = CascadeType.REMOVE)
    private SellerReply sellerReply;
}
