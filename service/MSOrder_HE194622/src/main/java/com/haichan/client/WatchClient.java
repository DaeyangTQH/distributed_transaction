package com.haichan.client;

import com.haichan.dto.ApiResponse;
import com.haichan.dto.StockBatchRequest;
import com.haichan.dto.StockRequest;
import com.haichan.dto.WatchResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "watch-service")
public interface WatchClient {

    @PostMapping("/api/v1/watches/{id}/reserve")
    ApiResponse<WatchResponse> reserve(
            @PathVariable("id") Integer id,
            @RequestBody StockRequest request);

    @PostMapping("/api/v1/watches/{id}/restore")
    ApiResponse<WatchResponse> restore(
            @PathVariable("id") Integer id,
            @RequestBody StockRequest request);

    @PostMapping("/api/v1/watches/reserve-batch")
    ApiResponse<Void> reserveBatch(@RequestBody StockBatchRequest request);

    @PostMapping("/api/v1/watches/restore-batch")
    ApiResponse<Void> restoreBatch(@RequestBody StockBatchRequest request);
}
