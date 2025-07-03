package com.wsd.ecommerce.presenter.api;


import com.wsd.ecommerce.core.service.SaleService;
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

@WebMvcTest(SaleController.class) // Focus on SaleController
public class SaleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean // Mock the SaleService dependency
    private SaleService saleService;

    @Test
    void getTotalSaleAmountForToday_shouldReturnTotalAmount_whenSalesExist() throws Exception {
        // Given
        LocalDate today = LocalDate.now();
        BigDecimal expectedTotal = new BigDecimal("2500.75");
        TotalSaleAmountResponse mockResponse = new TotalSaleAmountResponse(today, expectedTotal);

        when(saleService.getTotalSaleAmountForToday()).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/sales/today/total")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.date").value(today.toString())) // Assert date
                .andExpect(jsonPath("$.totalSaleAmount").value(expectedTotal.doubleValue())); // Assert total amount
    }

    @Test
    void getTotalSaleAmountForToday_shouldReturnZero_whenNoSalesExist() throws Exception {
        // Given
        LocalDate today = LocalDate.now();
        BigDecimal expectedTotal = BigDecimal.ZERO;
        TotalSaleAmountResponse mockResponse = new TotalSaleAmountResponse(today, expectedTotal);

        when(saleService.getTotalSaleAmountForToday()).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/sales/today/total")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.date").value(today.toString()))
                .andExpect(jsonPath("$.totalSaleAmount").value(expectedTotal.doubleValue()));
    }

    @Test
    void getTotalSaleAmountForToday_shouldReturnInternalServerError_whenServiceThrowsException() throws Exception {
        // Given
        when(saleService.getTotalSaleAmountForToday())
                .thenThrow(new RuntimeException("Database error during sale calculation."));

        // When & Then
        mockMvc.perform(get("/api/v1/sales/today/total")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError()) // Expect HTTP 500
                .andExpect(jsonPath("$.message").exists()); // Assert error message exists
    }
}
