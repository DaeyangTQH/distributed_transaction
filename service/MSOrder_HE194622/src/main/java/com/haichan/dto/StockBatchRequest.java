package com.haichan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockBatchRequest {
    private String idempotencyKey;
    private List<StockItemRequest> items;
}
