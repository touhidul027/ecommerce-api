package com.wsd.ecommerce.presenter.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaxSaleDayResponse {
    private LocalDate maxSaleDate;
    private BigDecimal maxSaleAmount;
}

