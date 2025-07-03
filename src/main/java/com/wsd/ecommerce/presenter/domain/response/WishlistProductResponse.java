package com.wsd.ecommerce.presenter.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WishlistProductResponse implements Serializable{
    @JsonProperty("nProducts")
    private BigDecimal nProducts;

    @JsonProperty("nPrice")
    private BigDecimal nPrice;
    private List<WishlistProduct> products;
}
