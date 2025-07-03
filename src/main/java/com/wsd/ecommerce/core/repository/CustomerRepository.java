package com.wsd.ecommerce.core.repository;

import com.wsd.ecommerce.core.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
}
