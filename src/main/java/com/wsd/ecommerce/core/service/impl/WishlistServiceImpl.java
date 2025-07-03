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

    private List<WishlistProduct> getWishlistByCustomerId(String customerId) {
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

        logger.trace(String.format("Attempting to retrieve wishlist for customer ID: %s", customerId));
        logger.trace(String.format("Products: %s", writeJsonString(products)));
        logger.error(String.format("WishList with Customer with ID %s not found.", customerId));

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
