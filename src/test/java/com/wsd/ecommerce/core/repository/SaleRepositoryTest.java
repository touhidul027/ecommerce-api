package com.wsd.ecommerce.core.repository;

import com.wsd.ecommerce.core.entity.Customer;
import com.wsd.ecommerce.core.entity.Product;
import com.wsd.ecommerce.core.entity.Sale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class SaleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SaleRepository saleRepository;

    private Customer customer;
    private Product product;

    @BeforeEach
    void setUp() {
        // Clear database before each test
        entityManager.clear();
        entityManager.getEntityManager().createQuery("DELETE FROM SaleItem").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM Sale").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM Customer").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM Product").executeUpdate();

        // Persist common test data
        Customer customer = Customer.builder()
                .customerId("cust-001")
                .name("John Doe")
                .email("john.doe@example.com")
                .address("123 Main St, Anytown, USA")
                .build();

        entityManager.persist(customer);

        Product product = Product.builder()
                .productId("prod-001")
                .name("Smartphone X")
                .description("Latest model with advanced camera")
                .price(new BigDecimal("999.99"))
                .category("Electronics")
                .createdAt(LocalDateTime.now())  // equivalent of SQL NOW()
                .build();

        entityManager.persist(product);

        entityManager.flush();
    }

    @Test
    void findBySaleDateBetween_shouldReturnSalesForGivenDay() {
        // Given
        LocalDate testDate = LocalDate.now();
        LocalDateTime startOfDay = testDate.atStartOfDay();
        LocalDateTime endOfDay = testDate.plusDays(1).atStartOfDay();

        // Sales for today
        Sale sale1 = new Sale(UUID.randomUUID().toString(), customer.getCustomerId(), startOfDay.plusHours(9), new BigDecimal("100.00"), "COMPLETED");
        Sale sale2 = new Sale(UUID.randomUUID().toString(), customer.getCustomerId(), startOfDay.plusHours(14), new BigDecimal("50.00"), "COMPLETED");
        entityManager.persist(sale1);
        entityManager.persist(sale2);

        // Sale for yesterday (should not be included)
        Sale saleYesterday = new Sale(UUID.randomUUID().toString(), customer.getCustomerId(), testDate.minusDays(1).atStartOfDay().plusHours(10), new BigDecimal("200.00"), "COMPLETED");
        entityManager.persist(saleYesterday);

        entityManager.flush();

        // When
        List<Sale> sales = saleRepository.findBySaleDateBetween(startOfDay, endOfDay);

        // Then
        assertNotNull(sales);
        assertEquals(2, sales.size());
        assertTrue(sales.stream().anyMatch(s -> s.getSaleId().equals(sale1.getSaleId())));
        assertTrue(sales.stream().anyMatch(s -> s.getSaleId().equals(sale2.getSaleId())));
        assertFalse(sales.stream().anyMatch(s -> s.getSaleId().equals(saleYesterday.getSaleId())));
    }

    @Test
    void findBySaleDateBetween_shouldReturnEmptyList_whenNoSalesForGivenDay() {
        // Given: No sales for today have been persisted

        LocalDate testDate = LocalDate.now().plusDays(5); // A day with no sales
        LocalDateTime startOfDay = testDate.atStartOfDay();
        LocalDateTime endOfDay = testDate.plusDays(1).atStartOfDay();

        // When
        List<Sale> sales = saleRepository.findBySaleDateBetween(startOfDay, endOfDay);

        // Then
        assertNotNull(sales);
        assertTrue(sales.isEmpty());
    }

    @Test
    void findBySaleDateBetween_shouldHandleSalesAtStartAndEndOfDay() {
        // Given
        LocalDate testDate = LocalDate.now();
        LocalDateTime startOfDay = testDate.atStartOfDay();
        LocalDateTime endOfDay = testDate.plusDays(1).atStartOfDay();

        Sale saleAtStart = new Sale(UUID.randomUUID().toString(), customer.getCustomerId(), startOfDay, new BigDecimal("10.00"), "COMPLETED");
        Sale saleJustBeforeEnd = new Sale(UUID.randomUUID().toString(), customer.getCustomerId(), endOfDay.minusNanos(1), new BigDecimal("20.00"), "COMPLETED");
        entityManager.persist(saleAtStart);
        entityManager.persist(saleJustBeforeEnd);
        entityManager.flush();

        // When
        List<Sale> sales = saleRepository.findBySaleDateBetween(startOfDay, endOfDay);

        // Then
        assertNotNull(sales);
        assertEquals(2, sales.size());
        assertTrue(sales.stream().anyMatch(s -> s.getSaleId().equals(saleAtStart.getSaleId())));
        assertTrue(sales.stream().anyMatch(s -> s.getSaleId().equals(saleJustBeforeEnd.getSaleId())));
    }
}

