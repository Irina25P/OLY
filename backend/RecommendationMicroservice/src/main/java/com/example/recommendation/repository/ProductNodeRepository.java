package com.example.recommendation.repository;

import com.example.recommendation.node.ProductNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductNodeRepository extends Neo4jRepository<ProductNode, String> {

    /**
     * Collaborative filtering: find products purchased by users with similar purchase history,
     * excluding products already purchased by the requesting user.
     */
    @Query("""
            MATCH (u:User {id: $userId})-[:PURCHASED]->(p:Product)
            MATCH (other:User)-[:PURCHASED]->(p)
            WHERE other.id <> $userId
            MATCH (other)-[:PURCHASED]->(rec:Product)
            WHERE NOT (u)-[:PURCHASED]->(rec) AND rec.id <> $userId
            RETURN DISTINCT rec
            ORDER BY rec.id
            LIMIT 20
            """)
    List<ProductNode> findCollaborativeRecommendations(@Param("userId") String userId);

    /**
     * Category-based fallback: products in the same categories the user has purchased from,
     * excluding already purchased products.
     */
    @Query("""
            MATCH (u:User {id: $userId})-[:PURCHASED]->(p:Product)
            WITH u, collect(DISTINCT p.category) AS preferredCategories
            MATCH (rec:Product)
            WHERE rec.category IN preferredCategories
              AND NOT (u)-[:PURCHASED]->(rec)
            RETURN DISTINCT rec
            LIMIT 20
            """)
    List<ProductNode> findCategoryBasedRecommendations(@Param("userId") String userId);

    /**
     * Popularity fallback: most purchased products overall for new users with no purchase history.
     */
    @Query("""
            MATCH (u:User)-[:PURCHASED]->(p:Product)
            RETURN p, count(u) AS purchaseCount
            ORDER BY purchaseCount DESC
            LIMIT 20
            """)
    List<ProductNode> findMostPurchasedProducts();
}
