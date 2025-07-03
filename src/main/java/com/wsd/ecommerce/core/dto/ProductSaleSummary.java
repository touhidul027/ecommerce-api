package com.wsd.ecommerce.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO used for direct projection from JPA queries to aggregate product sales.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSaleSummary {
    private String productId;
    private BigDecimal totalRevenue;
}
