package com.injibara.iams.backend.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ItemRequest {
    private String itemCode;
    private String itemName;
    private String description;
    private Long categoryId;
    private Long locationId;
    private String unit;
    private Integer quantity;
    private Integer minimumStock;
}
