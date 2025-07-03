package com.wsd.ecommerce.core.repository;

import com.wsd.ecommerce.core.entity.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, String> {
    // JpaRepository provides CRUD operations automatically
}
