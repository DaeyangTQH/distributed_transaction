package com.haichan.repository;

import com.haichan.entity.Watch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WatchRepository extends JpaRepository<Watch, Integer> {

    @Query("SELECT w FROM Watch w JOIN FETCH w.category ORDER BY w.watchId DESC")
    List<Watch> findAllWithCategoryOrderByIdDesc();

    @Query("SELECT w FROM Watch w JOIN FETCH w.category WHERE w.watchId = :id")
    Optional<Watch> findByIdWithCategory(@Param("id") Integer id);
}
