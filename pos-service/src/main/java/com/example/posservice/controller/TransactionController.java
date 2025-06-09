package com.example.posservice.controller;

import com.example.posservice.models.ProductDto;
import com.code.share.codesharing.model.ResultPage;
import com.example.posservice.models.ReceiptDto;
import com.example.posservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/list")
    public ResultPage<ProductDto> getProductList(@RequestBody Map<String, Object> params) {
        return transactionService.searchByNameOrSerial(params);
    }

    @PostMapping("/latest-receipt-number")
    public ResponseEntity<String> getLatestReceiptNumber() {
        String latestNumber = transactionService.getLatestReceiptNumber();
        return ResponseEntity.ok(latestNumber);
    }

    @PostMapping("/finalize")
    public ResponseEntity<String> finalizeReceipt(@RequestBody ReceiptDto receiptDto) {
        transactionService.finalizeReceipt(receiptDto);
        return ResponseEntity.ok("Receipt finalized successfully");
    }

    @PostMapping("/serialnumber-search")
    public ResponseEntity<List<String>> searchSerials(@RequestBody Map<String, Object> body) {
        Long productId = Long.valueOf(body.get("productId").toString());
        String keyword = body.get("keyword").toString();

        List<String> serials = transactionService.searchExactSerialNumber(productId, keyword);
        return ResponseEntity.ok(serials);
    }
}
