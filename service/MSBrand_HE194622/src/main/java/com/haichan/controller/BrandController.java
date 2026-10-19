package com.haichan.controller;

import com.haichan.dto.ApiResponse;
import com.haichan.dto.BrandResponse;
import com.haichan.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BrandResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok("Brands retrieved successfully", brandService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponse>> getById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Brand retrieved successfully", brandService.getById(id)));
    }
}
