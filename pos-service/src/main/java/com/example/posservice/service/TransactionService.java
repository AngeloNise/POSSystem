package com.example.posservice.service;

import com.example.posservice.models.ProductDto;
import com.code.share.codesharing.model.ResultPage;
import com.example.posservice.models.ReceiptDto;

import java.util.List;
import java.util.Map;

public interface TransactionService {

    ResultPage<ProductDto> searchByNameOrSerial(Map<String, Object> params);

    void finalizeReceipt(ReceiptDto receiptDto);

    String getLatestReceiptNumber();

    List<String> searchExactSerialNumber(Long productId, String keyword);

}
