package com.haichan.service;

import com.haichan.dto.BrandResponse;
import com.haichan.entity.WatchBrand;
import com.haichan.exception.ResourceNotFoundException;
import com.haichan.repository.WatchBrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final WatchBrandRepository watchBrandRepository;

    public List<BrandResponse> getAll() {
        return watchBrandRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public BrandResponse getById(Integer id) {
        WatchBrand brand = watchBrandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id));
        return toResponse(brand);
    }

    private BrandResponse toResponse(WatchBrand brand) {
        return BrandResponse.builder()
                .brandId(brand.getBrandId())
                .brandName(brand.getBrandName())
                .countryOfOrigin(brand.getCountryOfOrigin())
                .build();
    }
}
