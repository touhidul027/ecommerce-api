package com.wsd.ecommerce.presenter.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopSellingItemResponse {
    private String productId;
    private String productName;
    private BigDecimal totalRevenue;
}

