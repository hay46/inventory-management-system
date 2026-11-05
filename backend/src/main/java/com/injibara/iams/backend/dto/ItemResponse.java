package com.injibara.iams.backend.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ItemResponse {
    private Long id;
    private String itemCode;
    private String itemName;
    private String description;
    private Long categoryId;
    private String categoryName;
    private Long locationId;
    private String locationName;
    private String unit;
    private Integer quantity;
    private Integer minimumStock;
    private String status;
}
