package com.wsd.ecommerce.core.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "sale_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleItem {
    @Id
    private String saleItemId;
    private String saleId; // Foreign key to Sale
    private String productId; // Foreign key to Product
    private Integer quantity;
    private BigDecimal unitPriceAtSale; // Price at the time of sale
    private BigDecimal itemTotal; // quantity * unitPriceAtSale
}
