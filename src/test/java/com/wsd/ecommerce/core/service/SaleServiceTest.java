package com.wsd.ecommerce.core.service;

import com.wsd.ecommerce.core.entity.Sale;
import com.wsd.ecommerce.core.repository.SaleRepository;
import com.wsd.ecommerce.presenter.domain.response.TotalSaleAmountResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SaleServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @InjectMocks
    private SaleService saleService;

    private LocalDateTime todayStart;
    private LocalDateTime todayEnd;

    @BeforeEach
    void setUp() {
        LocalDate today = LocalDate.now();
        todayStart = today.atStartOfDay();
        todayEnd = today.plusDays(1).atStartOfDay(); // End of today is start of tomorrow
    }

    @Test
    void getTotalSaleAmountForToday_shouldReturnZero_whenNoSalesToday() {
        // Given
        when(saleRepository.findBySaleDateBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // When
        TotalSaleAmountResponse response = saleService.getTotalSaleAmountForToday();

        // Then
        assertNotNull(response);
        assertEquals(LocalDate.now(), response.getDate());
        assertEquals(BigDecimal.ZERO, response.getTotalSaleAmount());
    }

    @Test
    void getTotalSaleAmountForToday_shouldReturnCorrectSum_whenSalesExistToday() {
        // Given
        Sale sale1 = new Sale(UUID.randomUUID().toString(), "cust1", todayStart.plusHours(10), new BigDecimal("100.50"), "COMPLETED");
        Sale sale2 = new Sale(UUID.randomUUID().toString(), "cust2", todayStart.plusHours(14), new BigDecimal("200.25"), "COMPLETED");
        Sale sale3 = new Sale(UUID.randomUUID().toString(), "cust3", todayStart.plusHours(16), new BigDecimal("50.00"), "COMPLETED");

        List<Sale> salesToday = Arrays.asList(sale1, sale2, sale3);

        when(saleRepository.findBySaleDateBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(salesToday);

        // When
        TotalSaleAmountResponse response = saleService.getTotalSaleAmountForToday();

        // Then
        assertNotNull(response);
        assertEquals(LocalDate.now(), response.getDate());
        assertEquals(new BigDecimal("350.75"), response.getTotalSaleAmount()); // 100.50 + 200.25 + 50.00
    }

    @Test
    void getTotalSaleAmountForToday_shouldExcludeSalesFromOtherDays() {
        // Given
        Sale saleToday = new Sale(UUID.randomUUID().toString(), "cust1", todayStart.plusHours(10), new BigDecimal("100.00"), "COMPLETED");
        Sale saleYesterday = new Sale(UUID.randomUUID().toString(), "cust2", todayStart.minusDays(1).plusHours(10), new BigDecimal("50.00"), "COMPLETED");
        Sale saleTomorrow = new Sale(UUID.randomUUID().toString(), "cust3", todayStart.plusDays(1).plusHours(10), new BigDecimal("200.00"), "COMPLETED");

        List<Sale> salesRelevant = Collections.singletonList(saleToday); // Only today's sale should be returned by repo

        when(saleRepository.findBySaleDateBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(salesRelevant);

        // When
        TotalSaleAmountResponse response = saleService.getTotalSaleAmountForToday();

        // Then
        assertNotNull(response);
        assertEquals(LocalDate.now(), response.getDate());
        assertEquals(new BigDecimal("100.00"), response.getTotalSaleAmount());
    }
}
