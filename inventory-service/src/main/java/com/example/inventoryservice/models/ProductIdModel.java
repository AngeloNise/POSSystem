package com.example.inventoryservice.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductIdModel {

    private Integer productId;

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getProductId() {
        return productId;
    }
}
