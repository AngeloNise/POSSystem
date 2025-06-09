package com.example.possupplierservice.controller;

import com.example.possupplierservice.models.SupplierDto;
import com.example.possupplierservice.models.ResultMsg;
import com.code.share.codesharing.model.ResultPage;

import com.example.possupplierservice.service.SupplierService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/supplier")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @PostMapping("/create")
    public ResultMsg<SupplierDto> createSupplier(
            @RequestPart("supplier") SupplierDto supplierDto,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        try {
            if (image != null && !image.isEmpty()) {
                // Ensure folder exists
                String uploadDir = "universal-store-sakai/public/supplier-image";
                File directory = new File(uploadDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Create unique file name (e.g., with UUID or timestamp)
                String extension = FilenameUtils.getExtension(image.getOriginalFilename());
                String fileName = UUID.randomUUID().toString() + "." + extension;

                // Save the image
                Path imagePath = Paths.get(uploadDir).resolve(fileName);
                Files.copy(image.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);

                // Save file name to DTO
                supplierDto.setImage(fileName);
            }

            supplierService.createSupplier(supplierDto, image);
            return new ResultMsg<SupplierDto>().success(supplierDto, "Supplier created successfully");

        } catch (Exception e) {
            return new ResultMsg<SupplierDto>().failure(e.getMessage());
        }
    }


    @PostMapping("/update")
    public ResultMsg<SupplierDto> updateSupplier(
            @RequestPart("supplier") SupplierDto supplierDto,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        try {
            if (image != null && !image.isEmpty()) {
                String uploadDir = "universal-store-sakai/public/supplier-image";
                File directory = new File(uploadDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Generate unique file name
                String extension = FilenameUtils.getExtension(image.getOriginalFilename());
                String fileName = UUID.randomUUID().toString() + "." + extension;

                // Save new image file
                Path imagePath = Paths.get(uploadDir).resolve(fileName);
                Files.copy(image.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);

                // Optionally: delete old image file if exists (add logic here if needed)

                // Update image field on DTO
                supplierDto.setImage(fileName);
            }

            return supplierService.updateSupplier(supplierDto, image);

        } catch (Exception e) {
            return new ResultMsg<SupplierDto>().failure(e.getMessage());
        }
    }


    @PostMapping("/import")
    public ResultMsg<List<SupplierDto>> importSuppliers(@RequestParam("file") MultipartFile file) {
        return supplierService.importSuppliers(file);
    }

    @PostMapping("/export")
    public void exportSuppliers(HttpServletResponse response, @RequestBody Map<String, Object> params) {
        try {
            response.setContentType("application/octet-stream");
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=Suppliers_Export.xlsx";
            response.setHeader(headerKey, headerValue);
            supplierService.exportSuppliers(response.getOutputStream(), params);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/list")
    public ResultPage<SupplierDto> getSupplierList(@RequestBody Map<String, Object> params) {
        return supplierService.getSupplierList(params);
    }

    @PostMapping("/delete")
    public ResultMsg<?> deleteSupplier(@RequestParam Integer id) {
        supplierService.deleteSupplier(id);
        return new ResultMsg<>().success(null, "Supplier removed");
    }

    @PostMapping("/deleteMulti")
    public ResultMsg<?> deleteSuppliers(@RequestBody List<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ResultMsg<>().failure("No ids selected");
        }
        int removeCount = supplierService.deleteSuppliers(ids);
        return new ResultMsg<>().success(String.format("Removed %s supplier%s", removeCount, removeCount > 1 ? "s" : ""), "Operation successful");
    }

    @PostMapping("/importTemplate")
    public ResponseEntity<InputStreamResource> downloadImportTemplate() {
        InputStreamResource resource = new InputStreamResource(
                getClass().getClassLoader().getResourceAsStream("templates/Suppliers_Import.xlsx"));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Suppliers_Import.xlsx");

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }
}
