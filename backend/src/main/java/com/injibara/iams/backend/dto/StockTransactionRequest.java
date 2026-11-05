package com.injibara.iams.backend.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StockTransactionRequest {
    private Long itemId;
    private Integer quantity;
    private String referenceNumber;
    private String notes;
    private Long supplierId;
}
