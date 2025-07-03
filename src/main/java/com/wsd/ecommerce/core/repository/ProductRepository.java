package com.wsd.ecommerce.core.repository;

import com.wsd.ecommerce.core.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{

    @Query("SELECT p FROM Product p WHERE p.productId IN :productIds")
    List<Product> findAllByProductId(List<String> productIds);
}
