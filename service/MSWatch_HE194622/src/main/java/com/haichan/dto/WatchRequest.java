package com.haichan.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WatchRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 11, message = "Name must be greater than 10 characters")
    private String name;

    @NotNull(message = "CategoryID is required")
    private Integer categoryId;

    @NotNull(message = "BrandID is required")
    private Integer brandId;

    @NotBlank(message = "CaseMaterial is required")
    private String caseMaterial;

    @NotBlank(message = "StrapMaterial is required")
    private String strapMaterial;

    @NotBlank(message = "WaterResistance is required")
    private String waterResistance;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "100.00", message = "Price must be >= 100")
    private BigDecimal price;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock must be >= 0")
    private Integer stock;
}
