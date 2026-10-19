package com.haichan.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "StoreAccounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Integer accountId;

    @Column(name = "username", length = 20, nullable = false)
    private String username;

    @Column(name = "email", length = 100, nullable = false)
    private String email;

    @Column(name = "password", length = 100, nullable = false)
    private String password;

    /** 1 = Administrator, 2 = Staff, 3 = Member */
    @Column(name = "role")
    private Integer role;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
}
