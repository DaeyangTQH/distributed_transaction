package com.haichan.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "StockIdempotency",
        uniqueConstraints = @UniqueConstraint(
                name = "UK_StockIdempotency_Key_Op",
                columnNames = {"IdempotencyKey", "Operation"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockIdempotency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "IdempotencyKey", nullable = false, length = 100)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "Operation", nullable = false, length = 20)
    private StockOperation operation;

    @Column(name = "WatchID", nullable = false)
    private Integer watchId;

    @Column(name = "Quantity", nullable = false)
    private Integer quantity;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
