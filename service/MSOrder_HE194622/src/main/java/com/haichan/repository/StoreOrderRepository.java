package com.haichan.repository;

import com.haichan.entity.StoreOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StoreOrderRepository extends JpaRepository<StoreOrder, Integer> {

    @Query("SELECT DISTINCT o FROM StoreOrder o LEFT JOIN FETCH o.items ORDER BY o.orderId DESC")
    List<StoreOrder> findAllWithItems();

    @Query("SELECT DISTINCT o FROM StoreOrder o LEFT JOIN FETCH o.items WHERE o.accountId = :accountId ORDER BY o.orderId DESC")
    List<StoreOrder> findByAccountIdWithItems(@Param("accountId") Integer accountId);

    @Query("SELECT o FROM StoreOrder o LEFT JOIN FETCH o.items WHERE o.orderId = :id")
    Optional<StoreOrder> findByIdWithItems(@Param("id") Integer id);
}
