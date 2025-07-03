package com.wsd.ecommerce.presenter.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WishlistProduct implements Serializable {
    private String productId;
    private String name;
    private BigDecimal price;
}
