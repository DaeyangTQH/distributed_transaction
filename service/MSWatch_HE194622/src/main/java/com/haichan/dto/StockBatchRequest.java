package com.haichan.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class StockBatchRequest {

    @NotBlank(message = "IdempotencyKey is required")
    @Size(max = 100, message = "IdempotencyKey must be <= 100 characters")
    private String idempotencyKey;

    @NotEmpty(message = "At least one item is required")
    @Valid
    private List<StockItemRequest> items;
}
