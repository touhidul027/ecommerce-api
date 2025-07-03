package com.wsd.ecommerce.presenter.api;


import com.wsd.ecommerce.core.service.SaleService;
import com.wsd.ecommerce.presenter.domain.request.WishlistRequest;
import com.wsd.ecommerce.presenter.domain.response.*;
import com.wsd.ecommerce.presenter.model.ApiResponse;
import com.wsd.ecommerce.presenter.model.ResponseMessage;
import com.wsd.ecommerce.presenter.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sales")
public class SaleController extends BaseResource{

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
    public ApiResponse<TotalSaleAmountResponse> getTotalSaleAmountForToday() {
        try {
            TotalSaleAmountResponse response = saleService.getTotalSaleAmountForToday();
            return ResponseUtils.createSuccessResponseObject(
                    getMessage(ResponseMessage.OPERATION_SUCCESSFUL),
                    response
            );
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
    public ApiResponse<MaxSaleDayResponse> getMaxSaleDay(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate) {
        try {
            MaxSaleDayResponse response = saleService.getMaxSaleDay(startDate, endDate);
            return ResponseUtils.createSuccessResponseObject(
                    getMessage(ResponseMessage.OPERATION_SUCCESSFUL),
                    response
            );
        } catch (IllegalArgumentException e) {
            // For validation errors like invalid date range
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            System.err.println("Error retrieving max sale day for range " + startDate + " to " + endDate + ": " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve max sale day.", e);
        }
    }


    /**
     * GET /api/v1/sales/top-selling-items
     * Returns the top 5 selling items of all time based on total sale amount.
     *
     * @return A list of TopSellingItemResponse objects.
     */
    @GetMapping("/top-selling-items")
    public ApiResponse<List<TopSellingItemResponse>> getTopSellingItems() {
        try {
            List<TopSellingItemResponse> topItems = saleService.getTopSellingItems();
            return ResponseUtils.createSuccessResponseObject(
                    getMessage(ResponseMessage.OPERATION_SUCCESSFUL),
                    topItems
            );
        } catch (Exception e) {
            System.err.println("Error retrieving top selling items: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve top selling items.", e);
        }
    }


    /**
     * GET /api/v1/sales/top-selling-items/last-month-by-quantity
     * Returns the top 5 selling items of the last month based on total number of units sold.
     *
     * @return A list of TopSellingItemByQuantityResponse objects.
     */
    @GetMapping("/top-selling-items/last-month-by-quantity")
    public ApiResponse<List<TopSellingItemByQuantityResponse>> getTopSellingItemsLastMonthByQuantity() {
        try {
            List<TopSellingItemByQuantityResponse> topItems = saleService.getTopSellingItemsLastMonthByQuantity();
            return ResponseUtils.createSuccessResponseObject(
                    getMessage(ResponseMessage.OPERATION_SUCCESSFUL),
                    topItems
            );
        } catch (Exception e) {
            System.err.println("Error retrieving top selling items by quantity for last month: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve top selling items by quantity for last month.", e);
        }
    }
}
