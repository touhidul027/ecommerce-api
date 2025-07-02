package com.wsd.ecommerce.presenter.api;

import com.wsd.ecommerce.core.service.WishlistService;
import com.wsd.ecommerce.presenter.domain.request.WishlistRequest;
import com.wsd.ecommerce.presenter.domain.response.WishlistProduct;
import com.wsd.ecommerce.presenter.domain.response.WishlistProductResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @PostMapping("/wishlist")
    public ResponseEntity<WishlistProductResponse> getCustomerWishlist(@RequestBody WishlistRequest request) {
        WishlistProductResponse wishlistProductResponse
                = wishlistService.getWishlistResponseByCustomerId(request.getCustomerId());
        return ResponseEntity.ok(wishlistProductResponse);
    }
}
