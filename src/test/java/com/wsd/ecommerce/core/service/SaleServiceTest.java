package com.wsd.ecommerce.core.service;

import com.wsd.ecommerce.core.entity.Sale;
import com.wsd.ecommerce.core.repository.SaleRepository;
import com.wsd.ecommerce.presenter.domain.response.MaxSaleDayResponse;
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
        todayEnd = today.plusDays(1).atStartOfDay();
    }

    @Test
    void getTotalSaleAmountForToday_shouldReturnZero_whenNoSalesToday() {
        when(saleRepository.findBySaleDateBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        TotalSaleAmountResponse response = saleService.getTotalSaleAmountForToday();
        assertNotNull(response);
        assertEquals(LocalDate.now(), response.getDate());
        assertEquals(BigDecimal.ZERO, response.getTotalSaleAmount());
    }

    @Test
    void getTotalSaleAmountForToday_shouldReturnCorrectSum_whenSalesExistToday() {
        Sale sale1 = new Sale(UUID.randomUUID().toString(), "cust1", todayStart.plusHours(10), new BigDecimal("100.50"), "COMPLETED");
        Sale sale2 = new Sale(UUID.randomUUID().toString(), "cust2", todayStart.plusHours(14), new BigDecimal("200.25"), "COMPLETED");
        Sale sale3 = new Sale(UUID.randomUUID().toString(), "cust3", todayStart.plusHours(16), new BigDecimal("50.00"), "COMPLETED");
        List<Sale> salesToday = Arrays.asList(sale1, sale2, sale3);
        when(saleRepository.findBySaleDateBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(salesToday);
        TotalSaleAmountResponse response = saleService.getTotalSaleAmountForToday();
        assertNotNull(response);
        assertEquals(LocalDate.now(), response.getDate());
        assertEquals(new BigDecimal("350.75"), response.getTotalSaleAmount());
    }

    @Test
    void getTotalSaleAmountForToday_shouldExcludeSalesFromOtherDays() {
        Sale saleToday = new Sale(UUID.randomUUID().toString(), "cust1", todayStart.plusHours(10), new BigDecimal("100.00"), "COMPLETED");
        Sale saleYesterday = new Sale(
                UUID.randomUUID().toString(),
                "cust2",
                todayStart.minusDays(1).plusHours(10),
                new BigDecimal("50.00"),
                "COMPLETED"
        );

        Sale saleTomorrow = new Sale(
                UUID.randomUUID().toString(),
                "cust3",
                todayStart.plusDays(1).plusHours(10),
                new BigDecimal("200.00"),
                "COMPLETED"
        );



        List<Sale> salesRelevant = Collections.singletonList(saleToday);
        when(saleRepository.findBySaleDateBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(salesRelevant);
        TotalSaleAmountResponse response = saleService.getTotalSaleAmountForToday();
        assertNotNull(response);
        assertEquals(LocalDate.now(), response.getDate());
        assertEquals(new BigDecimal("100.00"), response.getTotalSaleAmount());
    }

    @Test
    void getMaxSaleDay_shouldReturnMaxSaleDay_whenSalesExistInDateRange() {
        LocalDate startDate = LocalDate.of(2025, 6, 1);
        LocalDate endDate = LocalDate.of(2025, 6, 3);

        Sale saleJune1 = new Sale(UUID.randomUUID().toString(), "cust1", startDate.atStartOfDay().plusHours(10), new BigDecimal("100.00"), "COMPLETED");
        Sale saleJune2_1 = new Sale(UUID.randomUUID().toString(), "cust2", startDate.plusDays(1).atStartOfDay().plusHours(12), new BigDecimal("250.00"), "COMPLETED");
        Sale saleJune2_2 = new Sale(UUID.randomUUID().toString(), "cust3", startDate.plusDays(1).atStartOfDay().plusHours(15), new BigDecimal("100.00"), "COMPLETED");
        Sale saleJune3 = new Sale(UUID.randomUUID().toString(), "cust4", startDate.plusDays(2).atStartOfDay().plusHours(11), new BigDecimal("150.00"), "COMPLETED");

        List<Sale> salesInPeriod = Arrays.asList(saleJune1, saleJune2_1, saleJune2_2, saleJune3);

        when(saleRepository.findBySaleDateBetween(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay()))
                .thenReturn(salesInPeriod);

        MaxSaleDayResponse response = saleService.getMaxSaleDay(startDate, endDate);

        assertNotNull(response);
        assertEquals(LocalDate.of(2025, 6, 2), response.getMaxSaleDate());
        assertEquals(new BigDecimal("350.00"), response.getMaxSaleAmount());
    }

    @Test
    void getMaxSaleDay_shouldReturnZeroAmountAndNullDate_whenNoSalesInDateRange() {
        LocalDate startDate = LocalDate.of(2025, 7, 1);
        LocalDate endDate = LocalDate.of(2025, 7, 31);

        when(saleRepository.findBySaleDateBetween(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay()))
                .thenReturn(Collections.emptyList());

        MaxSaleDayResponse response = saleService.getMaxSaleDay(startDate, endDate);

        assertNotNull(response);
        assertNull(response.getMaxSaleDate());
        assertEquals(BigDecimal.ZERO, response.getMaxSaleAmount());
    }

    @Test
    void getMaxSaleDay_shouldThrowIllegalArgumentException_whenStartDateIsAfterEndDate() {
        LocalDate startDate = LocalDate.of(2025, 6, 30);
        LocalDate endDate = LocalDate.of(2025, 6, 1);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            saleService.getMaxSaleDay(startDate, endDate);
        });
        assertEquals("Start date cannot be after end date.", thrown.getMessage());
    }

    @Test
    void getMaxSaleDay_shouldHandleSingleDayRange() {
        LocalDate singleDay = LocalDate.of(2025, 6, 10);
        Sale sale1 = new Sale(UUID.randomUUID().toString(), "cust1", singleDay.atStartOfDay().plusHours(10), new BigDecimal("123.45"), "COMPLETED");
        Sale sale2 = new Sale(UUID.randomUUID().toString(), "cust2", singleDay.atStartOfDay().plusHours(11), new BigDecimal("67.89"), "COMPLETED");
        List<Sale> sales = Arrays.asList(sale1, sale2);

        when(saleRepository.findBySaleDateBetween(singleDay.atStartOfDay(), singleDay.plusDays(1).atStartOfDay()))
                .thenReturn(sales);

        MaxSaleDayResponse response = saleService.getMaxSaleDay(singleDay, singleDay);

        assertNotNull(response);
        assertEquals(singleDay, response.getMaxSaleDate());
        assertEquals(new BigDecimal("191.34"), response.getMaxSaleAmount());
    }

    @Test
    void getMaxSaleDay_shouldReturnFirstDay_whenMultipleDaysHaveSameMaxAmount() {
        LocalDate startDate = LocalDate.of(2025, 6, 1);
        LocalDate endDate = LocalDate.of(2025, 6, 3);

        Sale saleJune1 = new Sale(UUID.randomUUID().toString(), "cust1", startDate.atStartOfDay().plusHours(10), new BigDecimal("200.00"), "COMPLETED");
        Sale saleJune2 = new Sale(UUID.randomUUID().toString(), "cust2", startDate.plusDays(1).atStartOfDay().plusHours(12), new BigDecimal("200.00"), "COMPLETED");
        Sale saleJune3 = new Sale(UUID.randomUUID().toString(), "cust3", startDate.plusDays(2).atStartOfDay().plusHours(11), new BigDecimal("100.00"), "COMPLETED");

        List<Sale> salesInPeriod = Arrays.asList(saleJune1, saleJune2, saleJune3);

        when(saleRepository.findBySaleDateBetween(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay()))
                .thenReturn(salesInPeriod);

        MaxSaleDayResponse response = saleService.getMaxSaleDay(startDate, endDate);

        assertNotNull(response);
        assertEquals(LocalDate.of(2025, 6, 1), response.getMaxSaleDate());
        assertEquals(new BigDecimal("200.00"), response.getMaxSaleAmount());
    }
}