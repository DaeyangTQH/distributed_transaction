package com.haichan.service;

import com.haichan.client.BrandClient;
import com.haichan.dto.StockRequest;
import com.haichan.dto.WatchRequest;
import com.haichan.dto.WatchResponse;
import com.haichan.entity.StockIdempotency;
import com.haichan.entity.StockOperation;
import com.haichan.entity.Watch;
import com.haichan.entity.WatchCategory;
import com.haichan.exception.BusinessException;
import com.haichan.exception.ConflictException;
import com.haichan.exception.ResourceNotFoundException;
import com.haichan.repository.StockIdempotencyRepository;
import com.haichan.repository.WatchCategoryRepository;
import com.haichan.repository.WatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WatchService {

    private final WatchRepository watchRepository;
    private final WatchCategoryRepository watchCategoryRepository;
    private final StockIdempotencyRepository stockIdempotencyRepository;
    private final BrandClient brandClient;

    @Transactional(readOnly = true)
    public List<WatchResponse> getAll() {
        return watchRepository.findAllWithCategoryOrderByIdDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public WatchResponse getById(Integer id) {
        Watch watch = watchRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new ResourceNotFoundException("Watch not found with id: " + id));
        return toResponse(watch);
    }

    @Transactional
    public WatchResponse create(WatchRequest request) {
        ensureBrandExists(request.getBrandId());
        WatchCategory category = findCategory(request.getCategoryId());

        Watch watch = Watch.builder()
                .name(request.getName())
                .category(category)
                .brandId(request.getBrandId())
                .caseMaterial(request.getCaseMaterial())
                .strapMaterial(request.getStrapMaterial())
                .waterResistance(request.getWaterResistance())
                .price(request.getPrice())
                .releaseDate(LocalDate.now())
                .stock(request.getStock())
                .build();

        return toResponse(watchRepository.save(watch));
    }

    @Transactional
    public WatchResponse update(Integer id, WatchRequest request) {
        Watch watch = watchRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new ResourceNotFoundException("Watch not found with id: " + id));

        ensureBrandExists(request.getBrandId());
        WatchCategory category = findCategory(request.getCategoryId());

        watch.setName(request.getName());
        watch.setCategory(category);
        watch.setBrandId(request.getBrandId());
        watch.setCaseMaterial(request.getCaseMaterial());
        watch.setStrapMaterial(request.getStrapMaterial());
        watch.setWaterResistance(request.getWaterResistance());
        watch.setPrice(request.getPrice());
        watch.setReleaseDate(LocalDate.now());
        watch.setStock(request.getStock());

        return toResponse(watchRepository.save(watch));
    }

    @Transactional
    public WatchResponse reserve(Integer id, StockRequest request) {
        return stockIdempotencyRepository
                .findByIdempotencyKeyAndOperation(request.getIdempotencyKey(), StockOperation.RESERVE)
                .map(existing -> replayReserve(existing, id, request.getQuantity()))
                .orElseGet(() -> doReserve(id, request));
    }

    private WatchResponse doReserve(Integer id, StockRequest request) {
        Watch watch = watchRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new ResourceNotFoundException("Watch not found with id: " + id));

        int quantity = request.getQuantity();
        int current = watch.getStock() == null ? 0 : watch.getStock();
        if (current < quantity) {
            throw new BusinessException("Insufficient stock for watch id: " + id);
        }
        watch.setStock(current - quantity);
        Watch saved = watchRepository.save(watch);

        stockIdempotencyRepository.save(StockIdempotency.builder()
                .idempotencyKey(request.getIdempotencyKey())
                .operation(StockOperation.RESERVE)
                .watchId(id)
                .quantity(quantity)
                .build());

        return toResponse(saved);
    }

    private WatchResponse replayReserve(StockIdempotency existing, Integer id, Integer quantity) {
        if (!existing.getWatchId().equals(id) || !existing.getQuantity().equals(quantity)) {
            throw new ConflictException("Idempotency key already used for a different reserve");
        }
        return getById(id);
    }

    @Transactional
    public WatchResponse restore(Integer id, StockRequest request) {
        return stockIdempotencyRepository
                .findByIdempotencyKeyAndOperation(request.getIdempotencyKey(), StockOperation.RESTORE)
                .map(existing -> replayRestore(existing, id, request.getQuantity()))
                .orElseGet(() -> doRestore(id, request));
    }

    private WatchResponse doRestore(Integer id, StockRequest request) {
        Watch watch = watchRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new ResourceNotFoundException("Watch not found with id: " + id));

        int quantity = request.getQuantity();
        int current = watch.getStock() == null ? 0 : watch.getStock();
        watch.setStock(current + quantity);
        Watch saved = watchRepository.save(watch);

        stockIdempotencyRepository.save(StockIdempotency.builder()
                .idempotencyKey(request.getIdempotencyKey())
                .operation(StockOperation.RESTORE)
                .watchId(id)
                .quantity(quantity)
                .build());

        return toResponse(saved);
    }

    private WatchResponse replayRestore(StockIdempotency existing, Integer id, Integer quantity) {
        if (!existing.getWatchId().equals(id) || !existing.getQuantity().equals(quantity)) {
            throw new ConflictException("Idempotency key already used for a different restore");
        }
        return getById(id);
    }

    @Transactional
    public void delete(Integer id) {
        if (!watchRepository.existsById(id)) {
            throw new ResourceNotFoundException("Watch not found with id: " + id);
        }
        watchRepository.deleteById(id);
    }

    private void ensureBrandExists(Integer brandId) {
        brandClient.getById(brandId);
    }

    private WatchCategory findCategory(Integer categoryId) {
        return watchCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
    }

    private WatchResponse toResponse(Watch watch) {
        return WatchResponse.builder()
                .name(watch.getName())
                .categoryId(watch.getCategory().getCategoryId())
                .categoryName(watch.getCategory().getCategoryName())
                .brandId(watch.getBrandId())
                .caseMaterial(watch.getCaseMaterial())
                .strapMaterial(watch.getStrapMaterial())
                .waterResistance(watch.getWaterResistance())
                .price(watch.getPrice())
                .releaseDate(watch.getReleaseDate())
                .stock(watch.getStock())
                .build();
    }
}
