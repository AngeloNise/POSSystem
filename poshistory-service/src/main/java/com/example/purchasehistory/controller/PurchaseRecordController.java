package com.example.purchasehistory.controller;

import com.example.purchasehistory.models.PurchaseRecordDto;
import com.example.purchasehistory.models.ReceiptDto;
import com.code.share.codesharing.model.ResultPage;
import com.example.purchasehistory.models.StoreDto;
import com.example.purchasehistory.service.PurchaseRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/purchase-record")
public class PurchaseRecordController {
    @Autowired
    private PurchaseRecordService purchaseRecordService;

    @PostMapping("/list")
    public ResultPage<PurchaseRecordDto> getPurchaseRecordList(@RequestBody Map<String, Object> params) {
        return purchaseRecordService.getPurchaseRecordList(params);
    }

    @DeleteMapping("/delete/{id}")
    public void deletePurchaseRecord(@PathVariable int id) {
        purchaseRecordService.deletePurchaseRecord(id);
    }

    @PostMapping("/receipt")
    public List<ReceiptDto> getReceipt(@RequestBody Map<String, Object> params) {
        String receiptNumber = (String) params.get("receiptNumber");
        return purchaseRecordService.getReceiptByNumber(receiptNumber);
    }

    @PostMapping("/get")
    public StoreDto getStore() {
        return purchaseRecordService.getStore();
    }
}
