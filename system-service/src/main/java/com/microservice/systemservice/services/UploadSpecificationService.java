package com.microservice.systemservice.services;

import com.microservice.systemservice.models.ResultMsg;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface UploadSpecificationService {

    ResultMsg<List<Map<String, Object>>> createUploadSpecification(String username, List<MultipartFile> files) throws IOException;

    ResultMsg<List<Map<String, Object>>> migrateUploadSpecificationToTable(List<Map<String, Object>> data, String username, String userId, String userFullName);

}
