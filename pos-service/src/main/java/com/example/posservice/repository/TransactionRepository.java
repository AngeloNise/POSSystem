package com.example.posservice.repository;

import com.example.posservice.models.ProductDto;
import com.example.posservice.models.Receipt;
import com.example.posservice.models.ReceiptItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface TransactionRepository {

    // From ProductRepository
    List<ProductDto> searchByNameOrSerial(Map<String, Object> params);

    int getTotalProductCount(Map<String, Object> params);

    void updateProductStock(int productId, int quantity);

    void updateItemStatusToSold(String serialNumber);

    // From ReceiptItemRepository
    void insertReceiptItem(ReceiptItem receiptItem);

    Integer findItemIdBySerialNumber(String serialNumber);

    void insertReceipt(Receipt receipt);

    String getLatestReceiptNumber();

    List<String> searchSerialByProductId(@Param("productId") Long productId, @Param("keyword") String keyword);

}
