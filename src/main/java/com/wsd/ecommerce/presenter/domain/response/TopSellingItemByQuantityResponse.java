package com.wsd.ecommerce.presenter.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopSellingItemByQuantityResponse {
    private String productId;
    private String productName;
    private Long totalQuantitySold; // Use Long for total quantity
}

