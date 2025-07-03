package com.wsd.ecommerce.presenter.api;


import com.wsd.ecommerce.core.service.SaleService;
import com.wsd.ecommerce.presenter.domain.response.MaxSaleDayResponse;
import com.wsd.ecommerce.presenter.domain.response.TotalSaleAmountResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/sales")
public class SaleController {

    private final SaleService saleService;

    @Autowired
    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    /**
     * GET /api/v1/sales/today/total
     * Returns the total sale amount for the current day.
     *
     * @return TotalSaleAmountResponse containing the date and total amount.
     */
    @GetMapping("/today/total")
    public ResponseEntity<TotalSaleAmountResponse> getTotalSaleAmountForToday() {
        try {
            TotalSaleAmountResponse response = saleService.getTotalSaleAmountForToday();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Log the exception for debugging purposes (optional, but good practice)
            System.err.println("Error retrieving total sales for today: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve total sales for today.", e);
        }
    }

    /**
     * GET /api/v1/sales/max-sale-day
     * Returns the day with the maximum sale amount within a specified date range.
     *
     * @param startDate The start date of the range (YYYY-MM-DD).
     * @param endDate   The end date of the range (YYYY-MM-DD).
     * @return MaxSaleDayResponse containing the date with max sales and the amount.
     */
    @GetMapping("/max-sale-day")
    public ResponseEntity<MaxSaleDayResponse> getMaxSaleDay(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate) {
        try {
            MaxSaleDayResponse response = saleService.getMaxSaleDay(startDate, endDate);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // For validation errors like invalid date range
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            System.err.println("Error retrieving max sale day for range " + startDate + " to " + endDate + ": " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve max sale day.", e);
        }
    }

}
