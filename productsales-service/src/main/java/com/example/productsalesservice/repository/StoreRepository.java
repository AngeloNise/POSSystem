package com.example.productsalesservice.repository;

import com.example.productsalesservice.models.StoreDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface StoreRepository {

    // Get the single store record (assumed only one)
    StoreDto getStore();

    // Update existing store record
    int updateStore(@Param("storeDto") StoreDto storeDto);

    // Insert new store record (if none exists)
    int insertStore(@Param("storeDto") StoreDto storeDto);

}
