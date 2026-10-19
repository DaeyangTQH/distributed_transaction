package com.haichan.controller;

import com.haichan.dto.ApiResponse;
import com.haichan.dto.WatchRequest;
import com.haichan.dto.WatchResponse;
import com.haichan.service.WatchService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/watches")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WatchController {

    WatchService watchService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<WatchResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok("Watches retrieved successfully", watchService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WatchResponse>> getById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Watch retrieved successfully", watchService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WatchResponse>> create(@Valid @RequestBody WatchRequest request) {
        WatchResponse created = watchService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Watch created successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WatchResponse>> update(
            @PathVariable("id") Integer id,
            @Valid @RequestBody WatchRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Watch updated successfully", watchService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") Integer id) {
        watchService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Watch deleted successfully", null));
    }
}
