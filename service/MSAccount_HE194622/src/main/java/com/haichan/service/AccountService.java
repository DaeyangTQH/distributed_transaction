package com.haichan.service;

import com.haichan.dto.LoginRequest;
import com.haichan.dto.LoginResponse;
import com.haichan.entity.StoreAccount;
import com.haichan.exception.AuthException;
import com.haichan.repository.StoreAccountRepository;
import com.haichan.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AccountService {

    StoreAccountRepository storeAccountRepository;
    JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        StoreAccount account = storeAccountRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AuthException("Invalid username or password"));

        if (!account.getPassword().equals(request.getPassword())) {
            throw new AuthException("Invalid username or password");
        }

        if (Boolean.FALSE.equals(account.getIsActive())) {
            throw new AuthException("Account is inactive");
        }

        String token = jwtService.generateToken(account);

        return LoginResponse.builder()
                .username(account.getUsername())
                .email(account.getEmail())
                .role(account.getRole())
                .roleName(resolveRoleName(account.getRole()))
                .token(token)
                .message("Login successful")
                .build();
    }

    private String resolveRoleName(Integer role) {
        if (role == null) {
            return "Unknown";
        }
        return switch (role) {
            case 1 -> "Administrator";
            case 2 -> "Staff";
            case 3 -> "Member";
            default -> "Unknown";
        };
    }
}
