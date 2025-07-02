package com.wsd.ecommerce.core.service;


import com.wsd.ecommerce.presenter.domain.response.WishlistProduct;
import com.wsd.ecommerce.presenter.domain.response.WishlistProductResponse;

import java.util.List;

public interface WishlistService {

    List<WishlistProduct> getWishlistByCustomerId(String customerId);

    WishlistProductResponse getWishlistResponseByCustomerId(String customerId);
}
