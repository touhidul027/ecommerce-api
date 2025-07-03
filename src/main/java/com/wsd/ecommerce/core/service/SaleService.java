package com.wsd.ecommerce.core.service;

import com.wsd.ecommerce.core.entity.Sale;
import com.wsd.ecommerce.core.repository.SaleRepository;
import com.wsd.ecommerce.presenter.domain.response.MaxSaleDayResponse;
import com.wsd.ecommerce.presenter.domain.response.TotalSaleAmountResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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


    /**
     * Finds the day with the maximum total sale amount within a given date range.
     *
     * @param startDate The inclusive start date of the range.
     * @param endDate The inclusive end date of the range.
     * @return MaxSaleDayResponse containing the date with the highest sales and its amount.
     * @throws IllegalArgumentException if startDate is after endDate.
     */
    public MaxSaleDayResponse getMaxSaleDay(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
        List<Sale> salesInPeriod = saleRepository.findBySaleDateBetween(startDateTime, endDateTime);

        if (salesInPeriod.isEmpty()) {
            return new MaxSaleDayResponse(null, BigDecimal.ZERO);
        }

        Map<LocalDate, BigDecimal> dailySales = salesInPeriod.stream()
                .collect(Collectors.groupingBy(
                        sale -> sale.getSaleDate().toLocalDate(),
                        Collectors.reducing(BigDecimal.ZERO, Sale::getTotalAmount, BigDecimal::add)
                ));

        Optional<Map.Entry<LocalDate, BigDecimal>> maxEntry = dailySales.entrySet().stream()
                .max(Comparator.comparing(Map.Entry::getValue));

        if (maxEntry.isPresent()) {
            return new MaxSaleDayResponse(maxEntry.get().getKey(), maxEntry.get().getValue());
        } else {
            return new MaxSaleDayResponse(null, BigDecimal.ZERO);
        }
    }
}

