package com.example.purchasehistory.service.impl;

import com.example.purchasehistory.models.PurchaseRecordDto;
import com.code.share.codesharing.model.ResultPage;
import com.example.purchasehistory.models.ReceiptDto;
import com.example.purchasehistory.models.StoreDto;
import com.example.purchasehistory.service.PurchaseRecordService;
import com.example.purchasehistory.repository.PurchaseRecordRepository;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class PurchaseRecordServiceImpl implements PurchaseRecordService {

    @Autowired
    private PurchaseRecordRepository purchaseRecordRepository;

    @Override
    public ResultPage<PurchaseRecordDto> getPurchaseRecordList(Map<String, Object> params) {
        boolean hasSearchFilters = params.containsKey("receiptNumber") || params.containsKey("productName") ||
                params.containsKey("counter") || params.containsKey("createdBy");

        if (!hasSearchFilters) {
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            params.put("createdDate", today);
        }

        Integer start = MapUtils.getInteger(params, "start", 0);
        Integer limit = MapUtils.getInteger(params, "limit", 10);

        List<PurchaseRecordDto> purchaseRecords = purchaseRecordRepository.getPurchaseRecords(params);

        int totalCount = (start == 1 && purchaseRecords.size() < limit)
                ? purchaseRecords.size()
                : purchaseRecordRepository.getTotalPurchaseRecordCount(params);

        return new ResultPage<>(purchaseRecords, totalCount, limit, start);
    }
    @Override
    public void deletePurchaseRecord(int id) {
        purchaseRecordRepository.deletePurchaseRecord(id);
    }

    @Override
    public List<ReceiptDto> getReceiptByNumber(String receiptNumber) {
        return purchaseRecordRepository.findReceiptByNumber(receiptNumber);
    }

    @Override
    public StoreDto getStore() {
        // Assuming your mapper returns StoreDto directly (not an entity)
        return purchaseRecordRepository.getStore();
    }
}
