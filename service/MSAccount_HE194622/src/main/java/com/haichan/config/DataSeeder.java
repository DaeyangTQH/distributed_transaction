package com.haichan.config;

import com.haichan.entity.StoreAccount;
import com.haichan.repository.StoreAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final StoreAccountRepository storeAccountRepository;

    @Override
    public void run(String... args) {
        if (storeAccountRepository.count() > 0) {
            return;
        }

        List<StoreAccount> accounts = List.of(
                StoreAccount.builder()
                        .username("adminsys")
                        .email("admin@drawingpen.com.vn")
                        .password("@1")
                        .role(1)
                        .isActive(true)
                        .build(),
                StoreAccount.builder()
                        .username("staff02")
                        .email("staff02@drawingpen.com.vn")
                        .password("@1")
                        .role(2)
                        .isActive(true)
                        .build(),
                StoreAccount.builder()
                        .username("member01")
                        .email("member01@drawingpen.com.vn")
                        .password("@1")
                        .role(3)
                        .isActive(true)
                        .build(),
                StoreAccount.builder()
                        .username("member02")
                        .email("member02@drawingpen.com.vn")
                        .password("@1")
                        .role(3)
                        .isActive(false)
                        .build(),
                StoreAccount.builder()
                        .username("staff01")
                        .email("staff01@drawingpen.com.vn")
                        .password("@1")
                        .role(2)
                        .isActive(false)
                        .build()
        );

        storeAccountRepository.saveAll(accounts);
    }
}
