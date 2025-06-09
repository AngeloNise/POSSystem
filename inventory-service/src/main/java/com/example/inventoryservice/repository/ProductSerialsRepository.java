package com.example.inventoryservice.repository;

import com.example.inventoryservice.dto.ShowSerialNumbersDto;
import com.example.inventoryservice.models.ProductIdModel;
import com.example.inventoryservice.models.ProductIdSerialModel;
import com.example.inventoryservice.models.SerialNumberModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface ProductSerialsRepository {
    List<String> selectAllSerialNumbers(ShowSerialNumbersDto productId);
    void insertSerialNumbers(@Param("productId") Integer productId, @Param("serialNumbers") List<String> serialNumbers);
    void deleteSerialNumbers(@Param("productId") Integer productId, @Param("serialNumbers") List<String> serialNumbers);
}
