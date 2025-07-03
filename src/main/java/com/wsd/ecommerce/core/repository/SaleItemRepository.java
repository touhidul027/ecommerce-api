package com.wsd.ecommerce.core.repository;

import com.wsd.ecommerce.core.dto.ProductSaleSummary;
import com.wsd.ecommerce.core.entity.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, String> {
    // Custom query to find total sales amount grouped by product
    @Query("SELECT new com.wsd.ecommerce.core.dto.ProductSaleSummary(si.productId, SUM(si.itemTotal)) " +
            "FROM SaleItem si GROUP BY si.productId ORDER BY SUM(si.itemTotal) DESC")
    List<ProductSaleSummary> findTotalSalesByProduct();
}
