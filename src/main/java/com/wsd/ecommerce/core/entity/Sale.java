package com.wsd.ecommerce.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sale {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sale_id", nullable = false, length = 255)
    private String saleId;
    private String customerId; // Foreign key to Customer
    private LocalDateTime saleDate;
    private BigDecimal totalAmount; // Total amount of this sale
    private String status; // e.g., "COMPLETED", "PENDING"
}
