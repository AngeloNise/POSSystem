package com.example.productsalesservice.service.impl;

import com.example.productsalesservice.models.StoreDto;
import com.example.productsalesservice.repository.StoreRepository;
import com.example.productsalesservice.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class StoreServiceImpl implements StoreService {

    @Autowired
    private StoreRepository storeRepository;

    private final String uploadDir = "universal-store-sakai/public/store-image";

    @Override
    public StoreDto getStore() {
        // Assuming your mapper returns StoreDto directly (not an entity)
        return storeRepository.getStore();
    }

    @Override
    public StoreDto updateStore(StoreDto storeDto, MultipartFile logoFile) throws IOException {
        if (logoFile != null && !logoFile.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + logoFile.getOriginalFilename();
            Path path = Paths.get(uploadDir, fileName);
            Files.copy(logoFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            storeDto.setStoreLogo(fileName);
        }

        int updated = storeRepository.updateStore(storeDto);
        if (updated == 0) {
            // If no rows updated, insert new record instead
            storeRepository.insertStore(storeDto);
        }
        return storeDto;
    }
}
