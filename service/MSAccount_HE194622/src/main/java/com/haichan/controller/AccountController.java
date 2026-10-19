package com.haichan.controller;

import com.haichan.dto.ApiResponse;
import com.haichan.dto.LoginRequest;
import com.haichan.dto.LoginResponse;
import com.haichan.service.AccountService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountController {

    AccountService accountService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse data = accountService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", data));
    }
}
