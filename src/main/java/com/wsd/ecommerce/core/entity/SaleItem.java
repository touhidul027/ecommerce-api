package com.wsd.ecommerce.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "sale_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleItem {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sale_item_id", nullable = false, length = 255)
    private String saleItemId;

    private String saleId; // Foreign key to Sale
    private String productId; // Foreign key to Product
    private Integer quantity;
    private BigDecimal unitPriceAtSale; // Price at the time of sale
    private BigDecimal itemTotal; // quantity * unitPriceAtSale
}
