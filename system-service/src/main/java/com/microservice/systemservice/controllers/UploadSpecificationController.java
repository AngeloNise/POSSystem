package com.microservice.systemservice.controllers;

import com.microservice.systemservice.helper.CategoriesUtil;
import com.microservice.systemservice.models.ResultMsg;
import com.microservice.systemservice.services.ActivityLogService;
import com.microservice.systemservice.services.CategoryService;
import com.microservice.systemservice.services.UploadSpecificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/te-datadict-list/upload")
@Slf4j
public class UploadSpecificationController {

    private final UploadSpecificationService uploadSpecificationService;

    private final CategoryService categoryService;


    @PostMapping(value="/uploadSpecificationData", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    ResultMsg<List<Map<String, Object>>> uploadSpecificationData(MultipartHttpServletRequest multipartFile, @RequestParam Map<String, String> body) throws IOException {
        String username = body.get("username");
        String userId = body.get("userId");
        String userFullName = body.get("userFullName");
        List<MultipartFile> files = multipartFile.getFiles("excel[]");
        ResultMsg<List<Map<String, Object>>> excelResult = uploadSpecificationService.createUploadSpecification(username, files);
        if (excelResult.isSuccess()) {
            return uploadSpecificationService.migrateUploadSpecificationToTable(excelResult.getData(), username, userId, userFullName);
        } else {
            return excelResult;
        }

    }

    @PostMapping(value="/refreshCategoriesOnUtil")
    public ResultMsg<?> refreshCategoriesOnUtil() {
        ResultMsg<?> resultMsg = new ResultMsg<>();
        try {
            CategoriesUtil.AllCATEGORIES_LIST = categoryService.queryCategoriesAllList();
            CategoriesUtil.CATEGORIES_LIST = categoryService.queryCategoriesList();
            CategoriesUtil.TOOLSANDEQUIPMENT_LIST = categoryService.queryToolsAndEquipmentList();
            CategoriesUtil.BRANDANDMODEL_LIST = categoryService.queryBrandAndModelList();
            CategoriesUtil.MODEL_LIST = categoryService.queryModelList();
            CategoriesUtil.LVT_LIST = categoryService.queryLVTList();
            resultMsg.setSuccess(true);
            resultMsg.setMessage("Categories refreshed");
        } catch (Exception e) {
            resultMsg.setSuccess(false);
            resultMsg.setMessage(e.getMessage());
        }
        return resultMsg;
    }

    @PostMapping(value="/queryCategories")
    public ResultMsg<Object> queryCategories(@RequestParam String type) {
        ResultMsg<Object> resultMsg = new ResultMsg<>();
        try {
            switch (type) {
                case "all":
                    resultMsg.setData(CategoriesUtil.AllCATEGORIES_LIST);
                    break;
                case "1":
                    resultMsg.setData(CategoriesUtil.CATEGORIES_LIST);
                    break;
                case "2":
                    resultMsg.setData(CategoriesUtil.TOOLSANDEQUIPMENT_LIST);
                    break;
                case "3":
                    resultMsg.setData(CategoriesUtil.BRANDANDMODEL_LIST);
                    break;
                case "5":
                    resultMsg.setData(CategoriesUtil.MODEL_LIST);
                    break;
                case "6":
                    resultMsg.setData(CategoriesUtil.LVT_LIST);
                    break;
            }
            resultMsg.setSuccess(true);
        } catch (Exception e) {
            log.info("queryCategories.e: " + ExceptionUtils.getStackTrace(e));
            resultMsg.setSuccess(false);
            resultMsg.setMessage(e.getMessage());
        }
        return resultMsg;
    }

}
