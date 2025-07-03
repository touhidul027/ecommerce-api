package com.wsd.ecommerce.presenter.api;


import com.wsd.ecommerce.core.service.SaleService;
import com.wsd.ecommerce.presenter.domain.response.TotalSaleAmountResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
}
