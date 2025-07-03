package com.wsd.ecommerce.core.repository;

import com.wsd.ecommerce.core.entity.Customer;
import com.wsd.ecommerce.core.entity.Product;
import com.wsd.ecommerce.core.entity.Sale;
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
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SaleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SaleRepository saleRepository;

    private Customer customer;
    private Product product;


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
        entityManager.clear();
        entityManager.getEntityManager().createQuery("DELETE FROM SaleItem").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM Sale").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM Customer").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM Product").executeUpdate();

        // Persist common test data
         customer = Customer.builder()
                //.id(1L)
                .customerId("cust-001")
                .name("John Doe")
                .email("john.doe@example.com")
                .address("123 Main St, Anytown, USA")
                .build();

        entityManager.persist(customer);

         product = Product.builder()
                //.id(1L)
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
        LocalDate testDate = LocalDate.now();
        LocalDateTime startOfDay = testDate.atStartOfDay();
        LocalDateTime endOfDay = testDate.plusDays(1).atStartOfDay();

        Sale sale1 = Sale.builder()
                .saleId(UUID.randomUUID().toString())
                .customerId(customer.getCustomerId())
                .saleDate(startOfDay.plusHours(9))
                .totalAmount(new BigDecimal("100.00"))
                .status("COMPLETED")
                .build();

        Sale sale2 = Sale.builder()
                .saleId(UUID.randomUUID().toString())
                .customerId(customer.getCustomerId())
                .saleDate(startOfDay.plusHours(14))
                .totalAmount(new BigDecimal("50.00"))
                .status("COMPLETED")
                .build();

        entityManager.persist(sale1);
        entityManager.persist(sale2);

        Sale saleYesterday = Sale.builder()
                .saleId(UUID.randomUUID().toString())
                .customerId(customer.getCustomerId())
                .saleDate(testDate.minusDays(1).atStartOfDay().plusHours(10))
                .totalAmount(new BigDecimal("200.00"))
                .status("COMPLETED")
                .build();

        entityManager.persist(saleYesterday);

        entityManager.flush();

        List<Sale> sales = saleRepository.findBySaleDateBetween(startOfDay, endOfDay);

        assertNotNull(sales);
        assertEquals(2, sales.size());
        assertTrue(sales.stream().anyMatch(s -> s.getSaleId().equals(sale1.getSaleId())));
        assertTrue(sales.stream().anyMatch(s -> s.getSaleId().equals(sale2.getSaleId())));
        assertFalse(sales.stream().anyMatch(s -> s.getSaleId().equals(saleYesterday.getSaleId())));
    }

    @Test
    void findBySaleDateBetween_shouldReturnEmptyList_whenNoSalesForGivenDay() {
        LocalDate testDate = LocalDate.now().plusDays(5);
        LocalDateTime startOfDay = testDate.atStartOfDay();
        LocalDateTime endOfDay = testDate.plusDays(1).atStartOfDay();

        List<Sale> sales = saleRepository.findBySaleDateBetween(startOfDay, endOfDay);

        assertNotNull(sales);
        assertTrue(sales.isEmpty());
    }

    @Test
    void findBySaleDateBetween_shouldHandleSalesAtStartAndEndOfDay() {
        LocalDate testDate = LocalDate.now();
        LocalDateTime startOfDay = testDate.atStartOfDay();
        LocalDateTime endOfDay = testDate.plusDays(1).atStartOfDay();

        Sale saleAtStart = Sale.builder()
                .saleId(UUID.randomUUID().toString())
                .customerId(customer.getCustomerId())
                .saleDate(startOfDay)
                .totalAmount(new BigDecimal("10.00"))
                .status("COMPLETED")
                .build();

        Sale saleJustBeforeEnd = Sale.builder()
                .saleId(UUID.randomUUID().toString())
                .customerId(customer.getCustomerId())
                .saleDate(endOfDay.minusNanos(1))
                .totalAmount(new BigDecimal("20.00"))
                .status("COMPLETED")
                .build();

        entityManager.persist(saleAtStart);
        entityManager.persist(saleJustBeforeEnd);
        entityManager.flush();

        List<Sale> sales = saleRepository.findBySaleDateBetween(startOfDay, endOfDay);

        assertNotNull(sales);
        assertEquals(2, sales.size());
        assertTrue(sales.stream().anyMatch(s -> s.getSaleId().equals(saleAtStart.getSaleId())));
        assertTrue(sales.stream().anyMatch(s -> s.getSaleId().equals(saleJustBeforeEnd.getSaleId())));
    }
}

