package com.example.recommendation.node;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.annotation.Version;

@Node("Product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductNode {

    @Id
    private String id;

    @Version
    private Long version;

    private String name;
    private Double price;
    private String category;
    private String sellerId;
    private String firstImageName;

    public ProductNode(String id) {
        this.id = id;
    }
}
