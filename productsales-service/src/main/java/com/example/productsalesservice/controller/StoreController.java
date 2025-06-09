package com.example.productsalesservice.controller;

import com.example.productsalesservice.models.StoreDto;
import com.example.productsalesservice.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/store")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @PostMapping("/get")
    public StoreDto getStore() {
        return storeService.getStore();
    }

    @PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public StoreDto updateStore(
            @RequestPart("store") StoreDto storeDto,
            @RequestPart(value = "logoFile", required = false) MultipartFile logoFile
    ) throws IOException {
        return storeService.updateStore(storeDto, logoFile);
    }
}
