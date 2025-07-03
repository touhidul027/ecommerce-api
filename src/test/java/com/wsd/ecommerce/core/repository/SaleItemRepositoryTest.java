package com.wsd.ecommerce.core.repository;

import com.wsd.ecommerce.core.dto.ProductQuantitySummary;
import com.wsd.ecommerce.core.dto.ProductSaleSummary;
import com.wsd.ecommerce.core.entity.Customer;
import com.wsd.ecommerce.core.entity.Product;
import com.wsd.ecommerce.core.entity.Sale;
import com.wsd.ecommerce.core.entity.SaleItem;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
    private Sale saleLastMonth;
    private Sale saleCurrentMonth;

    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("ecommerce")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        postgres.start();
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {
        // Clear database before each test
        entityManager.clear();
        entityManager.getEntityManager().createQuery("DELETE FROM SaleItem").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM Sale").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM Customer").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM Product").executeUpdate();

        // Persist common test data
        customer = Customer.builder()

                .customerId(UUID.randomUUID().toString())
                .name("Test Customer")
                .email("test@example.com")
                .address("123 Main St")
                .build();
        entityManager.persist(customer);

        productA = Product.builder()

                .productId("prod3")
                .name("Product A")
                .description("Desc C")
                .price(BigDecimal.ZERO)
                .category("Cat C")
                .createdAt(LocalDateTime.now())
                .build();

        productB = Product.builder()

                .productId("prod1")
                .name("Product B")
                .description("Desc A")
                .price(BigDecimal.ZERO)
                .category("Cat A")
                .createdAt(LocalDateTime.now())
                .build();

        productC = Product.builder()

                .productId("prod6")
                .name("Product C")
                .description("Desc F")
                .price(BigDecimal.ZERO)
                .category("Cat F")
                .createdAt(LocalDateTime.now())
                .build();

        entityManager.persist(productA);
        entityManager.persist(productB);
        entityManager.persist(productC);

        sale1 = Sale.builder()
                .saleId(UUID.randomUUID().toString())
                .customerId(customer.getCustomerId())
                .saleDate(LocalDateTime.now().minusDays(1))
                .totalAmount(new BigDecimal("30.00"))
                .status("COMPLETED")
                .build();

        sale2 = Sale.builder()
                .saleId(UUID.randomUUID().toString())
                .customerId(customer.getCustomerId())
                .saleDate(LocalDateTime.now())
                .totalAmount(new BigDecimal("45.00"))
                .status("COMPLETED")
                .build();

        entityManager.persist(sale1);
        entityManager.persist(sale2);

        // Sales for last month and current month for quantity tests
        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        YearMonth currentMonth = YearMonth.now();

        saleLastMonth = Sale.builder()
                .saleId(UUID.randomUUID().toString())
                .customerId(customer.getCustomerId())
                .saleDate(lastMonth.atDay(15).atStartOfDay().plusHours(10))
                .totalAmount(BigDecimal.ZERO)
                .status("COMPLETED")
                .build();

        entityManager.persist(saleLastMonth);

        saleCurrentMonth = Sale.builder()
                .saleId(UUID.randomUUID().toString())
                .customerId(customer.getCustomerId())
                .saleDate(currentMonth.atDay(5).atStartOfDay().plusHours(10))
                .totalAmount(BigDecimal.ZERO)
                .status("COMPLETED")
                .build();

        entityManager.persist(saleCurrentMonth);

        entityManager.flush();
    }

    @Test
    void findTotalSalesByProduct_shouldReturnCorrectAggregatedSales() {
        // Given
        // Sale 1: Product A (10.00 * 2 = 20.00), Product B (20.00 * 1 = 20.00)
        entityManager.persist(SaleItem.builder()
                .saleItemId(UUID.randomUUID().toString())
                .saleId(sale1.getSaleId())
                .productId(productA.getProductId())
                .quantity(2)
                .unitPriceAtSale(productA.getPrice())
                .itemTotal(new BigDecimal("20.00"))
                .build());

        entityManager.persist(SaleItem.builder()
                .saleItemId(UUID.randomUUID().toString())
                .saleId(sale1.getSaleId())
                .productId(productB.getProductId())
                .quantity(1)
                .unitPriceAtSale(productB.getPrice())
                .itemTotal(new BigDecimal("20.00"))
                .build());

        entityManager.persist(SaleItem.builder()
                .saleItemId(UUID.randomUUID().toString())
                .saleId(sale2.getSaleId())
                .productId(productA.getProductId())
                .quantity(1)
                .unitPriceAtSale(productA.getPrice())
                .itemTotal(new BigDecimal("10.00"))
                .build());

        entityManager.persist(SaleItem.builder()
                .saleItemId(UUID.randomUUID().toString())
                .saleId(sale2.getSaleId())
                .productId(productC.getProductId())
                .quantity(3)
                .unitPriceAtSale(productC.getPrice())
                .itemTotal(new BigDecimal("15.00"))
                .build());

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
        entityManager.persist(
                 SaleItem.builder()
                        .saleItemId(UUID.randomUUID().toString())
                        .saleId(sale1.getSaleId())
                        .productId(productA.getProductId())
                        .quantity(1)
                        .unitPriceAtSale(productA.getPrice())
                        .itemTotal(new BigDecimal("10.00"))
                        .build()
        );
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

    @Test
    void findTotalQuantitySoldByProductAndDateRange_shouldReturnCorrectQuantitiesForLastMonth() {
        // Given
        LocalDateTime startOfLastMonth = YearMonth.now().minusMonths(1).atDay(1).atStartOfDay();
        LocalDateTime endOfLastMonth = YearMonth.now().minusMonths(1).atEndOfMonth().plusDays(1).atStartOfDay();

        // Sale items for last month
        entityManager.persist(SaleItem.builder()
                .saleItemId(UUID.randomUUID().toString())
                .saleId(saleLastMonth.getSaleId())
                .productId(productA.getProductId())
                .quantity(5)
                .unitPriceAtSale(productA.getPrice())
                .itemTotal(new BigDecimal("50.00"))
                .build());

        entityManager.persist(SaleItem.builder()
                .saleItemId(UUID.randomUUID().toString())
                .saleId(saleLastMonth.getSaleId())
                .productId(productB.getProductId())
                .quantity(3)
                .unitPriceAtSale(productB.getPrice())
                .itemTotal(new BigDecimal("60.00"))
                .build());

        entityManager.persist(SaleItem.builder()
                .saleItemId(UUID.randomUUID().toString())
                .saleId(saleLastMonth.getSaleId())
                .productId(productA.getProductId())
                .quantity(2)
                .unitPriceAtSale(productA.getPrice())
                .itemTotal(new BigDecimal("20.00"))
                .build());

        entityManager.flush();

        // Sale items for current month (should be excluded)
        entityManager.persist(SaleItem.builder()
                .saleItemId(UUID.randomUUID().toString())
                .saleId(saleCurrentMonth.getSaleId())
                .productId(productA.getProductId())
                .quantity(10)
                .unitPriceAtSale(productA.getPrice())
                .itemTotal(new BigDecimal("100.00"))
                .build());

        entityManager.flush();

        // When
        List<ProductQuantitySummary> summaries = saleItemRepository.findTotalQuantitySoldByProductAndDateRange(startOfLastMonth, endOfLastMonth);

        // Then
        assertNotNull(summaries);
        assertEquals(2, summaries.size()); // Only products A and B from last month

        ProductQuantitySummary summaryA = summaries.stream()
                .filter(s -> s.getProductId().equals(productA.getProductId()))
                .findFirst().orElse(null);
        assertNotNull(summaryA);
        assertEquals(7L, summaryA.getTotalQuantity()); // 5 + 2 = 7

        ProductQuantitySummary summaryB = summaries.stream()
                .filter(s -> s.getProductId().equals(productB.getProductId()))
                .findFirst().orElse(null);
        assertNotNull(summaryB);
        assertEquals(3L, summaryB.getTotalQuantity());
    }

    @Test
    void findTotalQuantitySoldByProductAndDateRange_shouldReturnEmptyList_whenNoSalesInDateRange() {
        // Given: No sales items for the specified range

        LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);
        LocalDateTime start = threeMonthsAgo.withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = threeMonthsAgo.withDayOfMonth(threeMonthsAgo.lengthOfMonth()).plusDays(1).atStartOfDay();

        // When
        List<ProductQuantitySummary> summaries = saleItemRepository.findTotalQuantitySoldByProductAndDateRange(start, end);

        // Then
        assertNotNull(summaries);
        assertTrue(summaries.isEmpty());
    }

    @Test
    void findTotalQuantitySoldByProductAndDateRange_shouldHandleSingleItemInDateRange() {
        // Given
        LocalDateTime startOfLastMonth = YearMonth.now().minusMonths(1).atDay(1).atStartOfDay();
        LocalDateTime endOfLastMonth = YearMonth.now().minusMonths(1).atEndOfMonth().plusDays(1).atStartOfDay();

        entityManager.persist(SaleItem.builder()
                .saleItemId(UUID.randomUUID().toString())
                .saleId(saleLastMonth.getSaleId())
                .productId(productC.getProductId())
                .quantity(15)
                .unitPriceAtSale(productC.getPrice())
                .itemTotal(new BigDecimal("75.00"))
                .build());

        entityManager.flush();

        // When
        List<ProductQuantitySummary> summaries = saleItemRepository.findTotalQuantitySoldByProductAndDateRange(startOfLastMonth, endOfLastMonth);

        // Then
        assertNotNull(summaries);
        assertEquals(1, summaries.size());
        ProductQuantitySummary summaryC = summaries.get(0);
        assertEquals(productC.getProductId(), summaryC.getProductId());
        assertEquals(15L, summaryC.getTotalQuantity());
    }
}

