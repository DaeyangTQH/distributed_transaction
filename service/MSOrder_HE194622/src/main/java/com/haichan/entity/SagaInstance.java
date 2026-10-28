package com.haichan.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "SagaInstances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SagaInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SagaID")
    private Integer sagaId;

    @Column(name = "OrderID", nullable = false)
    private Integer orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "Type", nullable = false, length = 30)
    private SagaType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false, length = 30)
    @Builder.Default
    private SagaInstanceStatus status = SagaInstanceStatus.STARTED;

    @Column(name = "ErrorMessage", length = 500)
    private String errorMessage;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "saga", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SagaStep> steps = new ArrayList<>();

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) {
            status = SagaInstanceStatus.STARTED;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
