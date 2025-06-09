package com.example.possupplierservice.repository;

import com.example.possupplierservice.models.SupplierDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SupplierRepository {

    void createSupplier(SupplierDto supplierDto);

    void updateSupplier(SupplierDto supplierDto);

    void importSuppliers(@Param("suppliers") List<SupplierDto> suppliers);

    SupplierDto getSupplier(Integer id);

    List<SupplierDto> getSuppliers(Map<String, Object> params);

    List<SupplierDto> getSuppliersOverall(Map<String, Object> params);

    int getTotalSuppliersCount(Map<String, Object> params);

    int deleteSupplier(Integer id);

    int deleteSuppliers(@Param("ids") List<Integer> ids);
}
