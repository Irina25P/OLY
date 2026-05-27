package com.example.recommendation.node;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.annotation.Version;

import java.util.ArrayList;
import java.util.List;

@Node("User")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserNode {

    @Id
    private String id;

    @Version
    private Long version;

    @Relationship(type = "PURCHASED", direction = Relationship.Direction.OUTGOING)
    private List<ProductNode> purchasedProducts = new ArrayList<>();

    @Relationship(type = "VIEWED", direction = Relationship.Direction.OUTGOING)
    private List<ProductNode> viewedProducts = new ArrayList<>();

    public UserNode(String id) {
        this.id = id;
    }
}
