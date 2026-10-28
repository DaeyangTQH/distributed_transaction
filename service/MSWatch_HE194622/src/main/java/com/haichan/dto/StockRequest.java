package com.haichan.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StockRequest {

    @NotBlank(message = "IdempotencyKey is required")
    @Size(max = 100, message = "IdempotencyKey must be <= 100 characters")
    private String idempotencyKey;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be >= 1")
    private Integer quantity;
}
