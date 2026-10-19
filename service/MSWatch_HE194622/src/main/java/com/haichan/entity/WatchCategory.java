package com.haichan.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "WatchCategories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WatchCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CategoryID")
    private Integer categoryId;

    @Column(name = "CategoryName", length = 100)
    private String categoryName;

    @Lob
    @Column(name = "Description")
    private String description;

    @Column(name = "MovementType", length = 50)
    private String movementType;

    @Column(name = "PriceRange", length = 100)
    private String priceRange;
}
