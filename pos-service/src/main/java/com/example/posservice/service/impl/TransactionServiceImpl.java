package com.example.posservice.service.impl;

import com.example.posservice.models.*;
import com.code.share.codesharing.model.ResultPage;
import com.example.posservice.repository.TransactionRepository;
import com.example.posservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public String getLatestReceiptNumber() {
        String lastReceipt = transactionRepository.getLatestReceiptNumber();
        if (lastReceipt == null) {
            return "0001";
        }
        int nextNumber = Integer.parseInt(lastReceipt) + 1;
        return String.format("%04d", nextNumber);
    }

    @Transactional
    @Override
    public void finalizeReceipt(ReceiptDto receiptDto) {
        if (receiptDto == null || receiptDto.getReceiptNumber() == null || receiptDto.getReceiptItems() == null) {
            throw new IllegalArgumentException("Invalid receipt data");
        }

        Receipt receipt = new Receipt();
        receipt.setReceiptNumber(receiptDto.getReceiptNumber());
        receipt.setTotalPrice(receiptDto.getTotalPrice());
        receipt.setCreatedBy(receiptDto.getCreatedBy());
        receipt.setCounter(receiptDto.getCounter());

        transactionRepository.insertReceipt(receipt);

        if (receipt.getId() == 0) {
            throw new IllegalStateException("Failed to insert Receipt, ID is null");
        }

        System.out.println("Inserted Receipt with ID: " + receipt.getId());

        for (ReceiptItemDto itemDto : receiptDto.getReceiptItems()) {
            Integer itemId = itemDto.getSerialNumber() != null
                    ? transactionRepository.findItemIdBySerialNumber(itemDto.getSerialNumber())
                    : null;

            System.out.println("Inserting ReceiptItem with productId: " + itemDto.getProductId() +
                    ", itemId: " + itemId + ", serialNumber: " + itemDto.getSerialNumber());

            ReceiptItem receiptItem = new ReceiptItem();
            receiptItem.setReceiptId(receipt.getId());
            receiptItem.setProductId(itemDto.getProductId());
            receiptItem.setProductName(itemDto.getProductName());
            receiptItem.setItemId(itemId);
            receiptItem.setSerialNumber(itemDto.getSerialNumber());
            receiptItem.setPrice(itemDto.getPrice());
            receiptItem.setQuantity(itemDto.getQuantity());
            receiptItem.setCreatedDate(itemDto.getCreatedDate());

            transactionRepository.insertReceiptItem(receiptItem);

            if (itemDto.getSerialNumber() == null) {
                transactionRepository.updateProductStock(itemDto.getProductId(), itemDto.getQuantity());
            } else {
                transactionRepository.updateItemStatusToSold(itemDto.getSerialNumber());
            }
        }
    }

    @Override
    public ResultPage<ProductDto> searchByNameOrSerial(Map<String, Object> params) {
        Integer start = MapUtils.getInteger(params, "start", 0);
        Integer limit = MapUtils.getInteger(params, "limit", 10);
        List<ProductDto> products = transactionRepository.searchByNameOrSerial(params);
        int totalCount = start == 1 && products.size() < limit
                ? products.size() : transactionRepository.getTotalProductCount(params);
        return new ResultPage<>(products, totalCount, limit, start);
    }

    @Override
    public List<String> searchExactSerialNumber(Long productId, String keyword) {
        return transactionRepository.searchSerialByProductId(productId, keyword);
    }
}
