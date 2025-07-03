package com.wsd.ecommerce.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductQuantitySummary {
    private String productId;
    private Long totalQuantity; // Use Long for total quantity
}

