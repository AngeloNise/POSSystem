package com.example.purchasehistory.repository;

import com.example.purchasehistory.models.PurchaseRecordDto;
import com.example.purchasehistory.models.ReceiptDto;
import com.example.purchasehistory.models.StoreDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface PurchaseRecordRepository {
    List<PurchaseRecordDto> getPurchaseRecords(Map<String, Object> params);
    int getTotalPurchaseRecordCount(Map<String, Object> params);
    void deletePurchaseRecord(int id);
    List<ReceiptDto> findReceiptByNumber(String receiptNumber);
    StoreDto getStore();
}
