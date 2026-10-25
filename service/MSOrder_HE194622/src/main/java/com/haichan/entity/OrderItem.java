package com.haichan.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "OrderItems")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrderItemID")
    private Integer orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OrderID", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private StoreOrder order;

    /** Logical reference to Watch service (DBWatch) — no cross-DB FK */
    @Column(name = "WatchID", nullable = false)
    private Integer watchId;

    @Column(name = "Quantity", nullable = false)
    private Integer quantity;

    /** Filled later by saga when Watch price is known; null/0 on local create */
    @Column(name = "UnitPrice", precision = 10, scale = 2)
    private BigDecimal unitPrice;
}
