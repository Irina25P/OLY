package com.example.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="seller_replies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellerReply {
    @Id
    @UuidGenerator
    private UUID id;
    
    @Column(nullable = false)
    private String reply;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "review_id")
    private Review review;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}