package com.wsd.ecommerce.presenter.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TotalSaleAmountResponse {
    private LocalDate date;
    private BigDecimal totalSaleAmount;
}
