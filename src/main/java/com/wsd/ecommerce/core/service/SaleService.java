package com.wsd.ecommerce.core.service;

import com.wsd.ecommerce.core.dto.ProductSaleSummary;
import com.wsd.ecommerce.core.entity.Product;
import com.wsd.ecommerce.core.entity.Sale;
import com.wsd.ecommerce.core.repository.ProductRepository;
import com.wsd.ecommerce.core.repository.SaleItemRepository;
import com.wsd.ecommerce.core.repository.SaleRepository;
import com.wsd.ecommerce.presenter.domain.response.MaxSaleDayResponse;
import com.wsd.ecommerce.presenter.domain.response.TopSellingItemResponse;
import com.wsd.ecommerce.presenter.domain.response.TotalSaleAmountResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SaleService {

    private final SaleRepository saleRepository;

    private final SaleItemRepository saleItemRepository;
    private final ProductRepository productRepository;

    @Autowired
    public SaleService(SaleRepository saleRepository,
                       SaleItemRepository saleItemRepository,
                       ProductRepository productRepository) {
        this.saleRepository = saleRepository;
        this.saleItemRepository = saleItemRepository;
        this.productRepository = productRepository;
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

    /**
     * Returns the top 5 selling items of all time based on total sale amount.
     *
     * @return A list of TopSellingItemResponse objects.
     */
    public List<TopSellingItemResponse> getTopSellingItems() {
        // 1. Get aggregated sales data from the repository
        List<ProductSaleSummary> productSaleSummaries = saleItemRepository.findTotalSalesByProduct();

        if (productSaleSummaries.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. Extract all product IDs from the summaries
        List<String> productIds = productSaleSummaries.stream()
                .map(ProductSaleSummary::getProductId)
                .collect(Collectors.toList());

        // 3. Fetch product details (names) in a single batch call
        List<Product> products = productRepository.findAllByProductId(productIds);

        // 4. Create a map for quick lookup of product names by ID
        Map<String, String> productNamesMap = products.stream()
                .collect(Collectors.toMap(Product::getProductId, Product::getName));

        // 5. Map summaries to TopSellingItemResponse, enrich with product name, sort, and limit
        return productSaleSummaries.stream()
                .map(summary -> new TopSellingItemResponse(
                        summary.getProductId(),
                        productNamesMap.getOrDefault(summary.getProductId(), "Unknown Product"), // Handle missing product name
                        summary.getTotalRevenue()
                ))
                .sorted(Comparator.comparing(TopSellingItemResponse::getTotalRevenue).reversed()) // Sort by revenue descending
                .limit(5) // Take top 5
                .collect(Collectors.toList());
    }
}

