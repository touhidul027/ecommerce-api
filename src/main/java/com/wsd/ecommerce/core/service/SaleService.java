package com.wsd.ecommerce.core.service;

import com.wsd.ecommerce.core.entity.Sale;
import com.wsd.ecommerce.core.repository.SaleRepository;
import com.wsd.ecommerce.presenter.domain.response.TotalSaleAmountResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SaleService {

    private final SaleRepository saleRepository;

    @Autowired
    public SaleService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    /**
     * Calculates the total sale amount for the current day.
     *
     * @return TotalSaleAmountResponse containing the current date and the aggregated total sale amount.
     */
    public TotalSaleAmountResponse getTotalSaleAmountForToday() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay(); // Represents the start of the next day

        // Fetch all sales for the current day
        List<Sale> salesToday = saleRepository.findBySaleDateBetween(startOfDay, endOfDay);

        // Sum the totalAmount of all sales
        BigDecimal totalAmount = salesToday.stream()
                .map(Sale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new TotalSaleAmountResponse(today, totalAmount);
    }
}

