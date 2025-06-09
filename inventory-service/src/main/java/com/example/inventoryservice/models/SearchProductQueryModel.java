package com.example.inventoryservice.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchProductQueryModel {
    private String serialNumber;
    private String productName;
    private String variation;
    private String modifiedBy;
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdDate;

}
