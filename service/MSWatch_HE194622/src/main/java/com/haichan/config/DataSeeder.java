package com.haichan.config;

import com.haichan.entity.Watch;
import com.haichan.entity.WatchCategory;
import com.haichan.repository.WatchCategoryRepository;
import com.haichan.repository.WatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final WatchCategoryRepository watchCategoryRepository;
    private final WatchRepository watchRepository;

    @Override
    public void run(String... args) {
        if (watchCategoryRepository.count() > 0) {
            return;
        }

        List<WatchCategory> categories = watchCategoryRepository.saveAll(List.of(
                WatchCategory.builder()
                        .categoryName("Dive Watches")
                        .description("Designed for underwater diving and feature high water resistance.")
                        .movementType("Automatic")
                        .priceRange("$500 - $10,000")
                        .build(),
                WatchCategory.builder()
                        .categoryName("Chronograph Watches")
                        .description("Watches with a stopwatch function in addition to the main display.")
                        .movementType("Automatic or Quartz")
                        .priceRange("$300 - $8,000")
                        .build(),
                WatchCategory.builder()
                        .categoryName("Dress Watches")
                        .description("Elegant and simple, intended to be worn with formal wear.")
                        .movementType("Automatic or Manual")
                        .priceRange("$400 - $20,000")
                        .build(),
                WatchCategory.builder()
                        .categoryName("Digital Watches")
                        .description("Watches with an LCD or LED screen that show time in a digital format.")
                        .movementType("Quartz")
                        .priceRange("$50 - $500")
                        .build(),
                WatchCategory.builder()
                        .categoryName("Pilot Watches")
                        .description("Originally designed for aviators, featuring large, easy-to-read dials.")
                        .movementType("Automatic")
                        .priceRange("$700 - $15,000")
                        .build()
        ));

        WatchCategory c1 = categories.get(0);
        WatchCategory c2 = categories.get(1);
        WatchCategory c3 = categories.get(2);
        WatchCategory c4 = categories.get(3);
        WatchCategory c5 = categories.get(4);

        watchRepository.saveAll(List.of(
                watch("Submariner Date", c1, 1, "Oystersteel", "Oystersteel", "300 meters", "9550.00", "2020-09-01", 50),
                watch("Speedmaster Moonwatch Professional", c2, 2, "Stainless Steel", "Stainless Steel", "50 meters", "7200.00", "2021-01-05", 75),
                watch("Presage Cocktail Time", c3, 3, "Stainless Steel", "Leather", "50 meters", "425.00", "2019-06-01", 200),
                watch("G-Shock DW-5600E-1V", c4, 4, "Resin", "Resin", "200 meters", "69.95", "1996-06-12", 500),
                watch("Carrera Calibre Heuer 02", c2, 5, "Stainless Steel", "Alligator Leather", "100 meters", "5900.00", "2022-03-30", 60),
                watch("Seamaster Diver 300M", c1, 2, "Stainless Steel", "Rubber", "300 meters", "5400.00", "2018-02-15", 120),
                watch("Prospex SPB143", c1, 3, "Stainless Steel", "Stainless Steel", "200 meters", "1200.00", "2020-07-01", 150),
                watch("Daytona", c2, 1, "Oystersteel", "Oystersteel", "100 meters", "14550.00", "2016-03-16", 30),
                watch("F-91W", c4, 4, "Resin", "Resin", "30 meters", "19.99", "1989-01-01", 1000),
                watch("IWC Pilot's Watch Mark XVIII", c5, 4, "Stainless Steel", "Leather", "60 meters", "4500.00", "2016-01-18", 80)
        ));
    }

    private Watch watch(
            String name,
            WatchCategory category,
            int brandId,
            String caseMaterial,
            String strapMaterial,
            String waterResistance,
            String price,
            String releaseDate,
            int stock) {
        return Watch.builder()
                .name(name)
                .category(category)
                .brandId(brandId)
                .caseMaterial(caseMaterial)
                .strapMaterial(strapMaterial)
                .waterResistance(waterResistance)
                .price(new BigDecimal(price))
                .releaseDate(LocalDate.parse(releaseDate))
                .stock(stock)
                .build();
    }
}
