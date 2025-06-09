package com.microservice.systemservice.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataDictionary {

    private Integer dataDictID;
    private int categoryId;
    private String category;
    private int teNameId;
    private String teName;
    private int itemCodeId;
    private String itemCode;
    private int brandId;
    private String brand;
    private int modelId;
    private String model;
    private String specification;
    private int lifeSpan;
    private String description;
    private String marketValue;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String createdBy;
    private String updatedBy;
    private int createdById;
    private int updatedById;


    private String code;
    private String codeName;
    private int parentId;
    private String parentName;
    private int instanceId;
    private String name;

    private String categoryString;
    private String teNameString;
    private String itemCodeString;
    private String brandString;
    private String modelString;

}
