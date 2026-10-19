package com.haichan.repository;

import com.haichan.entity.StoreAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreAccountRepository extends JpaRepository<StoreAccount, Integer> {
    Optional<StoreAccount> findByUsername(String username);
}
