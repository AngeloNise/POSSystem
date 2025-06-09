package com.microservice.systemservice.models;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadSpecification {
    private int id;
    private int excelFileIdentifierID;
    private String project;
    private String projectId;
    private String region;
    private String regionId;
    private String area;
    private String areaId;
    private String fileName;
    private String fileType;
    private String extension;
    private String uploadedById;
    private String uploadedBy;
    private String dateUpload;
    private String status;
    private String filePath;
}
