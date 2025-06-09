package com.example.inventoryservice.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SearchProductModel {
    private Integer start;
    private Integer limit;
    private String serialNumber;
    private String productName;
    private String variation;
    private String modifiedBy;
    private LocalDateTime createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdDate;
}
