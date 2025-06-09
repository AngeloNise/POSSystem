package com.microservice.systemservice.services.impl;

import com.google.common.io.Files;
import com.microservice.systemservice.helper.ConfigUtils;
import com.microservice.systemservice.helper.Excel.UploadSpecificationHelper;
import com.microservice.systemservice.models.*;
import com.microservice.systemservice.repository.UploadSpecificationRepository;
import com.microservice.systemservice.services.UploadSpecificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadSpecificationServiceImpl implements UploadSpecificationService {

    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList("xls", "xlsx", "csv");
    //private static final String UPLOAD_PATH = ConfigUtils.getValue("Specification.uploadPath");
    private static final String UPLOAD_PATH = "C:/InventorySystemFiles/Specification/";
    private static final String STATUS = "status";
    private static final String REMARKS = "remarks";


    private final UploadSpecificationRepository upSpecRepo;

    public ResultMsg<List<Map<String, Object>>> createUploadSpecification(String username, List<MultipartFile> files) {
        ResultMsg<List<Map<String, Object>>> response = new ResultMsg<>();
        List<Map<String, Object>> results = new ArrayList<>();
        if (!CollectionUtils.isEmpty(files)) {
            for (MultipartFile file : files) {

                String fileExtension = Files.getFileExtension(file.getOriginalFilename());

                if (!ALLOWED_FILE_TYPES.contains(fileExtension)) {
                    results.add(Map.of("file", file.getOriginalFilename()
                            ,STATUS, "Fail"
                            ,REMARKS,"Invalid file type"));
                    continue;
                }

                UploadSpecification upload = buildUploadSpecificationExcelFile(file,fileExtension,username);
                upSpecRepo.insertUploadSpecification(upload);

                LocalDate now = LocalDate.now();

                StringBuilder fileDir = new StringBuilder()
                        .append(upload.getProject()).append("/")
                        .append(now.getYear()).append("/")
                        .append(now.getMonthValue()).append("/")
                        .append(now.getDayOfMonth()).append("/")
                        .append(upload.getUploadedBy()).append("/");

                upload.setFilePath(fileDir + file.getOriginalFilename());
                upSpecRepo.updateFilePathSpec(upload.getId(),upload.getFilePath());

                String fileInDirStr = UPLOAD_PATH + fileDir;
                String fileInStr = UPLOAD_PATH + upload.getFilePath();

                try{
                    File fileInDir = new File(fileInDirStr);
                    File fileIn = new File(fileInStr);

                    if (!fileInDir.exists())fileInDir.mkdirs();
                    if (!fileIn.exists()) fileIn.createNewFile();
                    file.transferTo(fileIn);

                    results.add(Map.of("file", file.getOriginalFilename()
                            ,STATUS, "Success"
                            ,"id",upload.getId())
                    );
                    response.setSuccess(true);
                    response.setMessage("Upload success");
                }catch(IOException e){
                    results.add(
                            Map.of("file",file.getOriginalFilename()
                                    ,STATUS,"Fail"
                                    ,REMARKS, StringUtils.join("System exception",
                                            StringUtils.isNotEmpty(e.getLocalizedMessage()) ? ": " + e.getLocalizedMessage() : ""))
                    );
                    response.setSuccess(false);
                }

            }
        }
        response.setData(results);
        return response;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public ResultMsg<List<Map<String, Object>>> migrateUploadSpecificationToTable(List<Map<String, Object>> data, String username, String uploaderId, String userFullName) {
        ResultMsg<List<Map<String, Object>>> result = new ResultMsg<>();
        List<Map<String, Object>> resultList = new ArrayList<>();

        List<Integer> idList = data.stream().map(specUpload -> MapUtils.getInteger(specUpload,"id"))
                .collect(Collectors.toList());
        AtomicInteger updateCount = new AtomicInteger();
        AtomicInteger insertCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();
        if(!CollectionUtils.isEmpty(idList)){
            UploadSpecificationHelper excelHelper = new UploadSpecificationHelper();
            for (int i = 0, idListSize = idList.size(); i < idListSize; i++) {
                String id = String.valueOf(idList.get(i));
                try {
                    UploadSpecification uploadSpecification = upSpecRepo.getById(id);
                    String filePath = UPLOAD_PATH + uploadSpecification.getFilePath();

                    File file = new File(filePath);
                    excelHelper.setFile(file);
                    ResultMsg2<Boolean> validation = excelHelper.validate();

                    if (!validation.isSuccess() || !validation.getData()) {
                        //insertUploadSapLogs(id, validation.getMessage());
                        upSpecRepo.updateStatus(id, "Error");
                        resultList.add(
                                Map.of(
                                        "file", getFileNameWithExtension(uploadSpecification.getFileName(),
                                                uploadSpecification.getExtension()),
                                        STATUS, "Fail",
                                        REMARKS, validation.getMessage(),
                                        "insertCount",0,
                                        "updateCount",0
                                )
                        );

                        excelHelper.clear();
                        continue;
                    }

                    List<DataDictionary> dataDictionaryList = excelHelper.read();
                    if (CollectionUtils.isEmpty(dataDictionaryList)) {
                        //insertUploadSapLogs(id, "Empty file");
                        resultList.add(Map.of(
                                        "file", getFileNameWithExtension(uploadSpecification.getFileName(), uploadSpecification.getExtension()),
                                        STATUS, "Fail",
                                        REMARKS, "Empty file"
                                )
                        );
                        continue;
                    }

                    int rowNum = 1;
                    StringBuilder requirementMsg = new StringBuilder();
                    log.info("SPECIFICATION IMPORT | TOTAL: " + dataDictionaryList.size());

                    //for activity log
                    ActivityLog activityLog = new ActivityLog();
                    activityLog.setPageName("Data Dictionary");
                    activityLog.setAction("Imported Specification");
                    activityLog.setCreatedDate(new Date());
                    activityLog.setCreatedById(uploaderId);
                    activityLog.setCreatedByName(userFullName);

                    for (DataDictionary ups : dataDictionaryList) {
                        log.info("SPECIFICATION IMPORT | IMPORTING : " + rowNum);
                        ups.setCategoryString(ups.getCategory());
                        ups.setTeNameString(ups.getTeName());
                        ups.setBrandString(ups.getBrand());
                        ups.setModelString(ups.getModel());
                        Map checker = excelHelper.checker(rowNum,ups);
                        requirementMsg.append(checker.get("message"));
                        //if(!checker.get("catId").equals("")){
                        ups.setCategory(checker.get("catId").toString());
                        //}
                        //if(!checker.get("taeId").equals("")){
                        ups.setTeName(checker.get("taeId").toString());
                        //}
                        //if(!checker.get("bamId").equals("")){
                        ups.setBrand(checker.get("bamId").toString());
                        //}
                        //if(!checker.get("modId").equals("")){
                        ups.setModel(checker.get("modId").toString());
                        //}
                        if(!checker.get("lvtId").equals("")){
                            ups.setItemCode(checker.get("lvtId").toString());
                        }


                        ups.setCreatedById(Integer.valueOf(uploaderId));
                        ups.setCreatedBy(username);
                        ups.setUpdatedById(Integer.valueOf(uploaderId));
                        ups.setUpdatedBy(username);


                        if(checker.get("success").equals(true)){
                            upSpecRepo.specificationCreateUpload(ups);
                            insertCount.getAndIncrement();
                        } else {
                             updateCount.getAndIncrement(); // Temporary Error Counter
                        }
                        rowNum++;
                    }
                    upSpecRepo.insertActivityLog1(activityLog);
                    upSpecRepo.updateStatus(id, "Uploaded");
                    resultList.add(Map.of(
                                    "file", getFileNameWithExtension(uploadSpecification.getFileName(), uploadSpecification.getExtension()),
                                    STATUS, insertCount.intValue() == 0 && updateCount.intValue() == 0?"Error":"Success",
                                    REMARKS, requirementMsg,
                                    "insertCount",insertCount.intValue(),
                                    "updateCount",updateCount.intValue()
                            )
                    );
                    excelHelper.clear();
                    result.setSuccess(true);
                } catch (Exception e) {
                    excelHelper.clear();
                    result.setSuccess(false);
                    //insertUploadSapLogs(id, ExceptionUtils.getMessage(e));
                    upSpecRepo.updateStatus(id, "Error");
                    log.error("migrateUploadSpecificationToTable.e: " + ExceptionUtils.getStackTrace(e));
                }
            }
            result.setData(resultList);
        }
        return result;
    }

    private String getFileNameWithExtension(String fileName, String extension) {
        return String.format("%s.%s", fileName, extension);
    }

    private boolean isSpreadsheet(String extension) {
        return StringUtils.equals("xls", extension)
                || StringUtils.equals("xlsx", extension)
                || StringUtils.equals("csv", extension);
    }

    private UploadSpecification buildUploadSpecificationExcelFile(MultipartFile file, String fileExtension,String username) {
        Map org = upSpecRepo.getMapOrgByAccountName(username);
        String project = "";
        String region = "";
        String area = "";
        if(Objects.nonNull(org)){
            if(MapUtils.getString(org,"ORG_TYPE").equals("1")){
                region = MapUtils.getString(org,"ORGANIZATION_NAME");
            } else if(MapUtils.getString(org,"ORG_TYPE").equals("2")){
                region = MapUtils.getString(org,"REGION_NAME");
            } else if(MapUtils.getString(org,"ORG_TYPE").equals("3")){
                region = MapUtils.getString(org,"REGION_NAME");
                area = MapUtils.getString(org,"AREA_NAME");
            } else {
                project = MapUtils.getString(org,"ORGANIZATION_NAME");
                region = MapUtils.getString(org,"REGION_NAME");
                area = MapUtils.getString(org,"AREA_NAME");
            }
        }
        return UploadSpecification.builder()
                .project(project)
                .area(region)
                .region(area)
                .status(null)
                .uploadedBy(username)
                .dateUpload(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd KK:mm a")))
                .fileName(Files.getNameWithoutExtension(file.getOriginalFilename()))
                .fileType(file.getContentType())
                .extension(fileExtension)
                .excelFileIdentifierID(isSpreadsheet(fileExtension) ? 1:0)
                .build();
    }



}
