package com.haichan.config;

import com.haichan.entity.WatchBrand;
import com.haichan.repository.WatchBrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final WatchBrandRepository watchBrandRepository;

    @Override
    public void run(String... args) {
        if (watchBrandRepository.count() > 0) {
            return;
        }

        List<WatchBrand> brands = List.of(
                WatchBrand.builder().brandName("Rolex").countryOfOrigin("Switzerland").build(),
                WatchBrand.builder().brandName("Omega").countryOfOrigin("Switzerland").build(),
                WatchBrand.builder().brandName("Seiko").countryOfOrigin("Japan").build(),
                WatchBrand.builder().brandName("Casio").countryOfOrigin("Japan").build(),
                WatchBrand.builder().brandName("TAG Heuer").countryOfOrigin("Switzerland").build()
        );

        watchBrandRepository.saveAll(brands);
    }
}
