package com.example.posservice.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptItemDto {
    private int productId;
    private String productName;
    private Integer itemId;
    private String serialNumber;
    private double price;
    private int quantity;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    // Custom constructor to handle date conversion
    @JsonCreator
    public ReceiptItemDto(@JsonProperty("createdDate") String createdDate) {
        // Convert UTC to local time
        this.createdDate = LocalDateTime.parse(createdDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.createdDate = this.createdDate.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }
}
