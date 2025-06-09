package com.example.inventoryservice.services;

import com.code.share.codesharing.model.ResultMsg;
import com.example.inventoryservice.dto.ShowSerialNumbersDto;
import com.example.inventoryservice.models.ProductIdModel;
import com.example.inventoryservice.models.ProductIdSerialModel;
import com.example.inventoryservice.models.SerialNumberModel;

import java.util.List;

public interface SerialNumberService {
    SerialNumberModel showSerialNumbers(ShowSerialNumbersDto pIdModel);
    ResultMsg addSerialNumbers (ProductIdSerialModel pModel);

    ResultMsg deleteSerialNumbers(ProductIdSerialModel pModel);



}
