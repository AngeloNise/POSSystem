package com.example.inventoryservice.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductIdSerialModel {
    private Integer productId;
    private List<String> serialNumber;
}
