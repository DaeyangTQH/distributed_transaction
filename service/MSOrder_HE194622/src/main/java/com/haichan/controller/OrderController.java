package com.haichan.controller;

import com.haichan.dto.ApiResponse;
import com.haichan.dto.OrderRequest;
import com.haichan.dto.OrderResponse;
import com.haichan.saga.PlaceOrderSaga;
import com.haichan.security.AuthAttributes;
import com.haichan.service.OrderService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {

    OrderService orderService;
    PlaceOrderSaga placeOrderSaga;

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok("Orders retrieved successfully", orderService.getAll()));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMine(
            @RequestAttribute(AuthAttributes.ACCOUNT_ID) Integer accountId) {
        return ResponseEntity.ok(ApiResponse.ok("Orders retrieved successfully", orderService.getMine(accountId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getById(
            @PathVariable("id") Integer id,
            @RequestAttribute(AuthAttributes.ACCOUNT_ID) Integer accountId) {
        return ResponseEntity.ok(ApiResponse.ok("Order retrieved successfully", orderService.getById(id, accountId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> create(
            @RequestAttribute(AuthAttributes.ACCOUNT_ID) Integer accountId,
            @Valid @RequestBody OrderRequest request) {
        OrderResponse created = placeOrderSaga.execute(accountId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Order created successfully", created));
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<OrderResponse>> confirm(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Order confirmed successfully", orderService.confirm(id)));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancel(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Order cancelled successfully", orderService.cancel(id)));
    }
}
