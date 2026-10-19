package com.haichan.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Watches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Watch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WatchID")
    private Integer watchId;

    @Column(name = "Name", length = 255)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CategoryID", nullable = false)
    private WatchCategory category;

    /** Reference to Brand service (DBBrand) — no cross-DB FK */
    @Column(name = "BrandID", nullable = false)
    private Integer brandId;

    @Column(name = "CaseMaterial", length = 100)
    private String caseMaterial;

    @Column(name = "StrapMaterial", length = 100)
    private String strapMaterial;

    /** Assignment schema: WaterpMaterial; seed data uses water resistance values */
    @Column(name = "WaterpMaterial", length = 50)
    private String waterResistance;

    @Column(name = "Price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "ReleaseDate")
    private LocalDate releaseDate;

    @Column(name = "Stock")
    private Integer stock;
}
