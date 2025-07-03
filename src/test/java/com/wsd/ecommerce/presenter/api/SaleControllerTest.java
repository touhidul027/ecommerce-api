package com.wsd.ecommerce.presenter.api;

import com.wsd.ecommerce.core.service.SaleService;
import com.wsd.ecommerce.presenter.domain.response.MaxSaleDayResponse;
import com.wsd.ecommerce.presenter.domain.response.TopSellingItemByQuantityResponse;
import com.wsd.ecommerce.presenter.domain.response.TopSellingItemResponse;
import com.wsd.ecommerce.presenter.domain.response.TotalSaleAmountResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

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
    void getTopSellingItems_shouldReturnTop5Items_whenSalesExist() throws Exception {
        // Given
        List<TopSellingItemResponse> mockTopItems = List.of(
                new TopSellingItemResponse("prodA", "Product A", new BigDecimal("10000.00")),
                new TopSellingItemResponse("prodB", "Product B", new BigDecimal("8000.00")),
                new TopSellingItemResponse("prodC", "Product C", new BigDecimal("5000.00")),
                new TopSellingItemResponse("prodD", "Product D", new BigDecimal("3000.00")),
                new TopSellingItemResponse("prodE", "Product E", new BigDecimal("1000.00"))
        );
        when(saleService.getTopSellingItems()).thenReturn(mockTopItems);

        // When & Then
        mockMvc.perform(get("/api/v1/sales/top-selling-items")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].productId").value("prodA"))
                .andExpect(jsonPath("$[0].totalRevenue").value(10000.00))
                .andExpect(jsonPath("$.length()").value(5)); // Ensure 5 items are returned
    }

    @Test
    void getTopSellingItems_shouldReturnFewerItems_whenLessThan5SalesExist() throws Exception {
        // Given
        List<TopSellingItemResponse> mockTopItems = List.of(
                new TopSellingItemResponse("prodA", "Product A", new BigDecimal("1000.00")),
                new TopSellingItemResponse("prodB", "Product B", new BigDecimal("500.00"))
        );
        when(saleService.getTopSellingItems()).thenReturn(mockTopItems);

        // When & Then
        mockMvc.perform(get("/api/v1/sales/top-selling-items")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2)); // Ensure only 2 items are returned
    }

    @Test
    void getTopSellingItems_shouldReturnEmptyList_whenNoSalesExist() throws Exception {
        // Given
        when(saleService.getTopSellingItems()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/v1/sales/top-selling-items")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0)); // Expect an empty array
    }

    @Test
    void getTopSellingItemsLastMonthByQuantity_shouldReturnTop5ItemsOrderedByQuantity() throws Exception {
        // Given
        List<TopSellingItemByQuantityResponse> mockTopItems = List.of(
                new TopSellingItemByQuantityResponse("prodX", "Product X", 500L),
                new TopSellingItemByQuantityResponse("prodY", "Product Y", 400L),
                new TopSellingItemByQuantityResponse("prodZ", "Product Z", 300L),
                new TopSellingItemByQuantityResponse("prodA", "Product A", 200L),
                new TopSellingItemByQuantityResponse("prodB", "Product B", 100L)
        );
        when(saleService.getTopSellingItemsLastMonthByQuantity()).thenReturn(mockTopItems);

        // When & Then
        mockMvc.perform(get("/api/v1/sales/top-selling-items/last-month-by-quantity")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].productId").value("prodX"))
                .andExpect(jsonPath("$[0].totalQuantitySold").value(500))
                .andExpect(jsonPath("$.length()").value(5));
    }

    @Test
    void getTopSellingItemsLastMonthByQuantity_shouldReturnFewerItems_whenLessThan5SalesExist() throws Exception {
        // Given
        List<TopSellingItemByQuantityResponse> mockTopItems = List.of(
                new TopSellingItemByQuantityResponse("prodX", "Product X", 50L),
                new TopSellingItemByQuantityResponse("prodY", "Product Y", 30L)
        );
        when(saleService.getTopSellingItemsLastMonthByQuantity()).thenReturn(mockTopItems);

        // When & Then
        mockMvc.perform(get("/api/v1/sales/top-selling-items/last-month-by-quantity")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getTopSellingItemsLastMonthByQuantity_shouldReturnEmptyList_whenNoSalesExist() throws Exception {
        // Given
        when(saleService.getTopSellingItemsLastMonthByQuantity()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/v1/sales/top-selling-items/last-month-by-quantity")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

}
