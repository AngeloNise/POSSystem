package com.microservice.systemservice.repository;

import com.microservice.systemservice.models.DataDictionary;
import com.microservice.systemservice.models.ActivityLog;
import com.microservice.systemservice.models.UploadSpecification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;
@Mapper
@Repository
public interface UploadSpecificationRepository {
    void updateFilePathSpec(int id, String filePath);

    void insertUploadSpecification(UploadSpecification upload);

    Map getMapOrgByAccountName(String username);

    void specificationCreateUpload(DataDictionary ups);

    UploadSpecification getById(String id);

    void updateStatus(@Param("id") String id, @Param("status") String error);

    void insertActivityLog1(ActivityLog activityLog);
}
