package com.wsd.ecommerce.presenter.api;

import com.wsd.ecommerce.core.service.SaleService;
import com.wsd.ecommerce.presenter.domain.response.MaxSaleDayResponse;
import com.wsd.ecommerce.presenter.domain.response.TotalSaleAmountResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SaleController.class)
public class SaleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SaleService saleService;

    @Test
    void getTotalSaleAmountForToday_shouldReturnTotalAmount_whenSalesExist() throws Exception {
        LocalDate today = LocalDate.now();
        BigDecimal expectedTotal = new BigDecimal("2500.75");
        TotalSaleAmountResponse mockResponse = new TotalSaleAmountResponse(today, expectedTotal);
        when(saleService.getTotalSaleAmountForToday()).thenReturn(mockResponse);
        mockMvc.perform(get("/api/v1/sales/today/total").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.date").value(today.toString()))
                .andExpect(jsonPath("$.totalSaleAmount").value(expectedTotal.doubleValue()));
    }

    @Test
    void getTotalSaleAmountForToday_shouldReturnZero_whenNoSalesExist() throws Exception {
        LocalDate today = LocalDate.now();
        BigDecimal expectedTotal = BigDecimal.ZERO;
        TotalSaleAmountResponse mockResponse = new TotalSaleAmountResponse(today, expectedTotal);
        when(saleService.getTotalSaleAmountForToday()).thenReturn(mockResponse);
        mockMvc.perform(get("/api/v1/sales/today/total").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.date").value(today.toString()))
                .andExpect(jsonPath("$.totalSaleAmount").value(expectedTotal.doubleValue()));
    }

    @Test
    void getTotalSaleAmountForToday_shouldReturnInternalServerError_whenServiceThrowsException() throws Exception {
        when(saleService.getTotalSaleAmountForToday()).thenThrow(new RuntimeException("Database error during sale calculation."));
        mockMvc.perform(get("/api/v1/sales/today/total").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getMaxSaleDay_shouldReturnMaxSaleDay_whenValidRangeAndSalesExist() throws Exception {
        LocalDate startDate = LocalDate.of(2025, 6, 1);
        LocalDate endDate = LocalDate.of(2025, 6, 30);
        LocalDate maxSaleDate = LocalDate.of(2025, 6, 15);
        BigDecimal maxSaleAmount = new BigDecimal("5000.00");
        MaxSaleDayResponse mockResponse = new MaxSaleDayResponse(maxSaleDate, maxSaleAmount);

        when(saleService.getMaxSaleDay(startDate, endDate)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/sales/max-sale-day")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.maxSaleDate").value(maxSaleDate.toString()))
                .andExpect(jsonPath("$.maxSaleAmount").value(maxSaleAmount.doubleValue()));
    }

    @Test
    void getMaxSaleDay_shouldReturnZeroAmount_whenValidRangeAndNoSalesExist() throws Exception {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);
        MaxSaleDayResponse mockResponse = new MaxSaleDayResponse(null, BigDecimal.ZERO);

        when(saleService.getMaxSaleDay(startDate, endDate)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/sales/max-sale-day")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.maxSaleDate").doesNotExist())
                .andExpect(jsonPath("$.maxSaleAmount").value(BigDecimal.ZERO.doubleValue()));
    }

    @Test
    void getMaxSaleDay_shouldReturnBadRequest_whenStartDateIsAfterEndDate() throws Exception {
        LocalDate startDate = LocalDate.of(2025, 6, 30);
        LocalDate endDate = LocalDate.of(2025, 6, 1);

        when(saleService.getMaxSaleDay(startDate, endDate))
                .thenThrow(new IllegalArgumentException("Start date cannot be after end date."));

        mockMvc.perform(get("/api/v1/sales/max-sale-day")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Start date cannot be after end date."));
    }

    @Test
    void getMaxSaleDay_shouldReturnInternalServerError_whenServiceThrowsUnexpectedException() throws Exception {
        LocalDate startDate = LocalDate.of(2025, 6, 1);
        LocalDate endDate = LocalDate.of(2025, 6, 30);

        when(saleService.getMaxSaleDay(startDate, endDate))
                .thenThrow(new RuntimeException("Unexpected error during max sale day calculation."));

        mockMvc.perform(get("/api/v1/sales/max-sale-day")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").exists());
    }
}
