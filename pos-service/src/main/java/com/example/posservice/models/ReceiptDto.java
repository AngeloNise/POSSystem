package com.example.posservice.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptDto {
    private int id;
    private String receiptNumber;
    private double totalPrice;
    private String createdBy;
    private String counter;
    private List<ReceiptItemDto> receiptItems;
}
