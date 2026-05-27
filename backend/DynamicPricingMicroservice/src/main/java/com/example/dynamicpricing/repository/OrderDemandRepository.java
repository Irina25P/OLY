package com.example.dynamicpricing.repository;

import com.example.dynamicpricing.entity.OrderDemandRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderDemandRepository extends JpaRepository<OrderDemandRecord, UUID> {
    List<OrderDemandRecord> findByProductIdAndOrderedAtAfter(UUID productId, Timestamp after);
    int countByProductIdAndOrderedAtAfter(UUID productId, Timestamp after);
}
