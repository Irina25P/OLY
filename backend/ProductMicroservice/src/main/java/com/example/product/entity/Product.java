package com.example.product.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Product {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, name = "description")
    private String description;

    @Column(nullable = false, name = "price")
    private Double price;


    @Column(nullable=false, name = "name")
    private String name;

    @Column(nullable=false, name = "stock")
    private Integer stock;

    @ElementCollection
    @CollectionTable(name = "entity_images", joinColumns = @JoinColumn(name = "entity_id"))
    @Column(name = "image_name")
    private List<String> imageNames = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "category")
    private InterestType category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sellerId")
    private User seller;

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
    private List<Review> reviews = new ArrayList<>();
}
