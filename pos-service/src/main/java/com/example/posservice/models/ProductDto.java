package com.example.posservice.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private int id;
    private String image;
    private String name;
    private double price;
    private Integer stocks;
    private boolean serialize;
    private String serialNumber;
}
