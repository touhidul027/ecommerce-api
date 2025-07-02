package com.wsd.ecommerce.core.service.impl;

import com.wsd.ecommerce.core.entity.Product;
import com.wsd.ecommerce.core.entity.WishList;
import com.wsd.ecommerce.core.repository.ProductRepository;
import com.wsd.ecommerce.core.repository.WishListRepository;
import com.wsd.ecommerce.core.service.BaseService;
import com.wsd.ecommerce.core.service.WishlistService;
import com.wsd.ecommerce.presenter.domain.response.WishlistProduct;
import com.wsd.ecommerce.presenter.domain.response.WishlistProductResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Primary
public class WishlistServiceImpl extends BaseService implements WishlistService {
    private final WishListRepository wishListRepository;
    private final ProductRepository productRepository;

    @Override
    public List<WishlistProduct> getWishlistByCustomerId(String customerId) {
        List<WishList> wishListItems = wishListRepository.findByCustomerId(customerId);

        List<String> productIds = wishListItems.stream()
                .map(WishList::getProductId)
                .collect(Collectors.toList());

        List<Product> products = productRepository.findAllByProductId(productIds);

        return products.stream()
                .map(p -> new WishlistProduct(p.getProductId(), p.getName(), p.getPrice()))
                .toList();
    }

    @Override
    public WishlistProductResponse getWishlistResponseByCustomerId(String customerId) {
        List<WishlistProduct> products = this.getWishlistByCustomerId(customerId);

        logger.debug("Attempting to retrieve wishlist for customer");
        logger.trace("Attempting to retrieve wishlist for customer ID: {}", customerId);
        logger.info("Successfully found customer with ID: {}", customerId); // Info log
        logger.warn("WishList with Customer with ID {} not found.", customerId); // Warn log
        logger.error("Products Not exist but Wishlist associated products id found. Products IDs: {}.", products.toString());

        BigDecimal totalPrice = products.stream()
                .map(WishlistProduct::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalProducts = BigDecimal.valueOf(products.size());

        return WishlistProductResponse.builder()
                .nPrice(totalPrice)
                .nProducts(totalProducts)
                .products(products)
                .build();
    }

}
