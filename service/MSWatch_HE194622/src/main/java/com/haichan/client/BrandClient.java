package com.haichan.client;

import com.haichan.dto.ApiResponse;
import com.haichan.dto.BrandResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "brand-service")
public interface BrandClient {

    @GetMapping("/api/v1/brands/{id}")
    ApiResponse<BrandResponse> getById(@PathVariable("id") Integer id);
}
