package com.haichan.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {

    @NotEmpty(message = "At least one order item is required")
    @Valid
    private List<OrderItemRequest> items;
}
