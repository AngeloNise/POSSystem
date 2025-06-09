package com.example.productsalesservice.service;

import com.example.productsalesservice.models.StoreDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StoreService {

    StoreDto getStore();

    StoreDto updateStore(StoreDto storeDto, MultipartFile logoFile) throws IOException;
}

