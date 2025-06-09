package com.example.posservice.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Receipt {
    private int id;
    private String receiptNumber;
    private double totalPrice;
    private String createdBy;
    private String counter;
}
