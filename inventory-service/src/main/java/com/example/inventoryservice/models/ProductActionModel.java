package com.example.inventoryservice.models;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductActionModel {

    private Integer accountId;
    private Integer id;
    private String action;
    private Integer capital;
    private Boolean serialize;
    private String productName;
    private String productImage;
    private String variation;
    private Integer profit;
    private Integer stocks;
    private String createBy;
    private String modifiedBy;
    private ProductIdSerialModel serialData;

}
