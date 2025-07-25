package com.example.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShowSerialNumbersDto {
    private Integer productId;
    private String serialTerm;
    private Integer limit;
    private Integer start;


}
