package com.haichan.controller;

import com.haichan.dto.ApiResponse;
import com.haichan.dto.PaymentRequest;
import com.haichan.dto.PaymentResponse;
import com.haichan.dto.PaymentStatus;
import com.haichan.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> charge(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse payment = paymentService.charge(request);
        HttpStatus httpStatus = switch (payment.getStatus()) {
            case SUCCESS -> HttpStatus.OK;
            case FAILED -> HttpStatus.BAD_REQUEST;
            case TIMEOUT -> HttpStatus.GATEWAY_TIMEOUT;
        };
        String message = switch (payment.getStatus()) {
            case SUCCESS -> "Payment successful";
            case FAILED -> "Payment failed";
            case TIMEOUT -> "Payment timed out";
        };
        boolean ok = payment.getStatus() == PaymentStatus.SUCCESS;
        return ResponseEntity.status(httpStatus)
                .body(ApiResponse.<PaymentResponse>builder()
                        .success(ok)
                        .message(message)
                        .data(payment)
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Payment retrieved successfully", paymentService.getById(id)));
    }
}
