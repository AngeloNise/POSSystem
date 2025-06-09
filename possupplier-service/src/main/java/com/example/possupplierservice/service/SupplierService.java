package com.example.possupplierservice.service;

import com.example.possupplierservice.models.SupplierDto;
import com.example.possupplierservice.models.ResultMsg;
import com.code.share.codesharing.model.ResultPage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public interface SupplierService {
    void createSupplier(SupplierDto supplierDto, MultipartFile image) throws IOException;

    ResultMsg<SupplierDto> updateSupplier(SupplierDto supplierDto, MultipartFile image) throws IOException;

    ResultMsg<List<SupplierDto>> importSuppliers(MultipartFile file);

    void exportSuppliers(OutputStream outputStream, Map<String, Object> params) throws IOException;

    ResultPage<SupplierDto> getSupplierList(Map<String, Object> params);

    int deleteSupplier(Integer id);

    int deleteSuppliers(List<Integer> ids);
}
