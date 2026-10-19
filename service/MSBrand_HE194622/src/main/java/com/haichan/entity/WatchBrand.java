package com.haichan.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "WatchBrands")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WatchBrand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BrandID")
    Integer brandId;

    @Column(name = "BrandName", length = 200)
    String brandName;

    @Column(name = "CountryOfOrigin", length = 50)
    String countryOfOrigin;
}
