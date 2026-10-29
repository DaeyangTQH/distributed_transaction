package com.haichan.repository;

import com.haichan.entity.StockIdempotency;
import com.haichan.entity.StockOperation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockIdempotencyRepository extends JpaRepository<StockIdempotency, Integer> {

    Optional<StockIdempotency> findByIdempotencyKeyAndOperation(String idempotencyKey, StockOperation operation);
}
