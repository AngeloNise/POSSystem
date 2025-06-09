package com.example.inventoryservice.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ProductPageModel {
        private Integer id;
        private Integer capital;
        private Boolean serialize;
        private String serialNumber;
        private String productName;
        private String productImage;
        private String variation;
        private Integer profit;
        private Integer stocks;
        private String createBy;
        private String modifiedBy;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDateTime createdDate;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDateTime modifiedDate;
}
