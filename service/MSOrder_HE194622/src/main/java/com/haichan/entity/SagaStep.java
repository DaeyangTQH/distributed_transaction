package com.haichan.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "SagaSteps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SagaStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "StepID")
    private Integer stepId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SagaID", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private SagaInstance saga;

    @Enumerated(EnumType.STRING)
    @Column(name = "Name", nullable = false, length = 30)
    private SagaStepName name;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false, length = 20)
    private SagaStepStatus status;

    @Column(name = "WatchID")
    private Integer watchId;

    @Column(name = "Quantity")
    private Integer quantity;

    @Column(name = "IdempotencyKey", length = 100)
    private String idempotencyKey;

    @Column(name = "ErrorMessage", length = 500)
    private String errorMessage;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
