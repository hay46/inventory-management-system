package com.injibara.iams.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String itemCode;

    @Column(nullable = false, length = 150)
    private String itemName;

    @Column(length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id")
    private Location location;

    @Builder.Default
    @Column(length = 50)
    private String unit = "Piece";

    @Builder.Default
    @Column(nullable = false)
    private Integer quantity = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer minimumStock = 0;

    @Builder.Default
    @Column(nullable = false)
    private String status = "ACTIVE";

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;
}
