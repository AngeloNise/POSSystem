package com.example.inventoryservice.models;

import org.apache.ibatis.type.Alias;

public class UploadImageModel {
    private String imgPath;
    private Integer productId;

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getProductId() {
        return productId;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}
