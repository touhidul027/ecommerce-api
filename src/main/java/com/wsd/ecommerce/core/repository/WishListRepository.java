package com.wsd.ecommerce.core.repository;

import com.wsd.ecommerce.core.entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishListRepository extends JpaRepository<WishList, Long> {
    List<WishList> findByCustomerId(String customerId);
}
