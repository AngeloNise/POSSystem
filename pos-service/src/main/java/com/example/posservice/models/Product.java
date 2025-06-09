package com.example.posservice.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private int id;
    private String image;
    private String name;
    private double price;
    private Integer stocks;
    private boolean serialize;
    private String serialNumber;
}
