package com.microservice.systemservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Categories {

    private Integer categoryId;
    private String categoryName;
    private String code;
    private LocalDateTime createdDate;
    private String createdBy;
    private String createdId;
    private String remarks;
    private  String parentId;
    private  String instanceId;

}
