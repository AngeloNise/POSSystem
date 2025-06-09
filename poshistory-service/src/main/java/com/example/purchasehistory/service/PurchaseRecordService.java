package com.example.purchasehistory.service;

import com.example.purchasehistory.models.PurchaseRecordDto;
import com.example.purchasehistory.models.ReceiptDto;
import com.code.share.codesharing.model.ResultPage;
import com.example.purchasehistory.models.StoreDto;

import java.util.Map;
import java.util.List;


public interface PurchaseRecordService {
    ResultPage<PurchaseRecordDto> getPurchaseRecordList(Map<String, Object> params);
    void deletePurchaseRecord(int id);
    List<ReceiptDto> getReceiptByNumber(String receiptNumber);
    StoreDto getStore();

}
