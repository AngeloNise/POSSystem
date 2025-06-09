package com.example.inventoryservice.dto;

import com.code.share.codesharing.model.PagingRequest;
import com.example.inventoryservice.models.SearchProductQueryModel;
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
public class SearchProductDto {
    private String serialNumber;
    private String productName;
    private String variation;
    private String modifiedBy;
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdDate;

    private Integer start;
    private Integer limit;
    public SearchProductDto (PagingRequest<SearchProductQueryModel> pRequest) {

        SearchProductQueryModel sQuery;
        sQuery = pRequest.getQuery();


        this.serialNumber = sQuery.getSerialNumber();
        this.productName = sQuery.getProductName();
        this.variation = sQuery.getVariation();
        this.modifiedBy = sQuery.getModifiedBy();
        this.createdBy = sQuery.getCreatedBy();
        this.createdDate = sQuery.getCreatedDate();
        this.limit = pRequest.getLimit();
        this.start = pRequest.getStart();

    }

}
