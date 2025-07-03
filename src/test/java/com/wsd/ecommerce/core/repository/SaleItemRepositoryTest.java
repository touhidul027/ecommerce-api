package com.wsd.ecommerce.core.repository;

import com.wsd.ecommerce.core.dto.ProductSaleSummary;
import com.wsd.ecommerce.core.entity.Customer;
import com.wsd.ecommerce.core.entity.Product;
import com.wsd.ecommerce.core.entity.Sale;
import com.wsd.ecommerce.core.entity.SaleItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class SaleItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SaleItemRepository saleItemRepository;

    private Customer customer;
    private Product productA;
    private Product productB;
    private Product productC;
    private Sale sale1;
    private Sale sale2;

    @BeforeEach
    void setUp() {
        // Clear database before each test
        entityManager.clear();
        entityManager.getEntityManager().createQuery("DELETE FROM SaleItem").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM Sale").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM Customer").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM Product").executeUpdate();

        // Persist common test data
        customer = new Customer(1L, UUID.randomUUID().toString(), "Test Customer", "test@example.com", "123 Main St");
        entityManager.persist(customer);

        productA = new Product(1L, "prod3", "Product A", "Desc C", BigDecimal.ZERO, "Cat C", LocalDateTime.now());
        productB = new Product(2L, "prod1", "Product B", "Desc A", BigDecimal.ZERO, "Cat A", LocalDateTime.now());
        productC = new Product(3L, "prod6", "Product C", "Desc F", BigDecimal.ZERO, "Cat F", LocalDateTime.now());

        entityManager.persist(productA);
        entityManager.persist(productB);
        entityManager.persist(productC);

        sale1 = new Sale(UUID.randomUUID().toString(), customer.getCustomerId(), LocalDateTime.now().minusDays(1), new BigDecimal("30.00"), "COMPLETED");
        sale2 = new Sale(UUID.randomUUID().toString(), customer.getCustomerId(), LocalDateTime.now(), new BigDecimal("45.00"), "COMPLETED");
        entityManager.persist(sale1);
        entityManager.persist(sale2);

        entityManager.flush();
    }

    @Test
    void findTotalSalesByProduct_shouldReturnCorrectAggregatedSales() {
        // Given
        // Sale 1: Product A (10.00 * 2 = 20.00), Product B (20.00 * 1 = 20.00)
        entityManager.persist(new SaleItem(UUID.randomUUID().toString(), sale1.getSaleId(), productA.getProductId(), 2, productA.getPrice(), new BigDecimal("20.00")));
        entityManager.persist(new SaleItem(UUID.randomUUID().toString(), sale1.getSaleId(), productB.getProductId(), 1, productB.getPrice(), new BigDecimal("20.00")));

        // Sale 2: Product A (10.00 * 1 = 10.00), Product C (5.00 * 3 = 15.00)
        entityManager.persist(new SaleItem(UUID.randomUUID().toString(), sale2.getSaleId(), productA.getProductId(), 1, productA.getPrice(), new BigDecimal("10.00")));
        entityManager.persist(new SaleItem(UUID.randomUUID().toString(), sale2.getSaleId(), productC.getProductId(), 3, productC.getPrice(), new BigDecimal("15.00")));
        entityManager.flush();

        // Expected totals:
        // Product A: 20.00 + 10.00 = 30.00
        // Product B: 20.00
        // Product C: 15.00

        // When
        List<ProductSaleSummary> summaries = saleItemRepository.findTotalSalesByProduct();

        // Then
        assertNotNull(summaries);
        assertEquals(3, summaries.size()); // Should have 3 unique products

        // Verify Product A
        ProductSaleSummary summaryA = summaries.stream()
                .filter(s -> s.getProductId().equals(productA.getProductId()))
                .findFirst().orElse(null);
        assertNotNull(summaryA);
        assertEquals(new BigDecimal("30.00"), summaryA.getTotalRevenue());

        // Verify Product B
        ProductSaleSummary summaryB = summaries.stream()
                .filter(s -> s.getProductId().equals(productB.getProductId()))
                .findFirst().orElse(null);
        assertNotNull(summaryB);
        assertEquals(new BigDecimal("20.00"), summaryB.getTotalRevenue());

        // Verify Product C
        ProductSaleSummary summaryC = summaries.stream()
                .filter(s -> s.getProductId().equals(productC.getProductId()))
                .findFirst().orElse(null);
        assertNotNull(summaryC);
        assertEquals(new BigDecimal("15.00"), summaryC.getTotalRevenue());
    }

    @Test
    void findTotalSalesByProduct_shouldReturnEmptyList_whenNoSaleItemsExist() {
        // Given: No sale items persisted

        // When
        List<ProductSaleSummary> summaries = saleItemRepository.findTotalSalesByProduct();

        // Then
        assertNotNull(summaries);
        assertTrue(summaries.isEmpty());
    }

    @Test
    void findTotalSalesByProduct_shouldHandleSingleSaleItem() {
        // Given
        entityManager.persist(new SaleItem(UUID.randomUUID().toString(), sale1.getSaleId(), productA.getProductId(), 1, productA.getPrice(), new BigDecimal("10.00")));
        entityManager.flush();

        // When
        List<ProductSaleSummary> summaries = saleItemRepository.findTotalSalesByProduct();

        // Then
        assertNotNull(summaries);
        assertEquals(1, summaries.size());
        ProductSaleSummary summaryA = summaries.get(0);
        assertEquals(productA.getProductId(), summaryA.getProductId());
        assertEquals(new BigDecimal("10.00"), summaryA.getTotalRevenue());
    }
}

