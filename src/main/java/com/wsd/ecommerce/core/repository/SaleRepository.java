package com.wsd.ecommerce.core.repository;


import com.wsd.ecommerce.core.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, String> {
    // Custom method to find sales within a specific date range
    List<Sale> findBySaleDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
}