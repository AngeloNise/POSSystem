package com.example.possupplierservice.service.impl;

import com.example.possupplierservice.models.SupplierDto;
import com.example.possupplierservice.models.ResultMsg;
import com.code.share.codesharing.model.ResultPage;
import com.example.possupplierservice.repository.SupplierRepository;
import com.example.possupplierservice.service.SupplierService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class SupplierServiceImpl implements SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    private final String uploadDir = "universal-store-sakai/public/supplier-image";

    @Override
    public void createSupplier(SupplierDto supplierDto, MultipartFile image) throws IOException {
        if (image != null && !image.isEmpty()) {
            String fileName = saveImage(image);
            supplierDto.setImage(fileName);
        }
        supplierRepository.createSupplier(supplierDto);
    }

    @Override
    public ResultMsg<SupplierDto> updateSupplier(SupplierDto supplierDto, MultipartFile image) throws IOException {
        SupplierDto current = supplierRepository.getSupplier(supplierDto.getId());
        if (current == null) {
            return new ResultMsg<SupplierDto>().failure("Supplier does not exist");
        }

        if (image != null && !image.isEmpty()) {
            // Optionally delete old image here if needed:
            if (current.getImage() != null && !current.getImage().isEmpty()) {
                Path oldImagePath = Paths.get(uploadDir).resolve(current.getImage());
                Files.deleteIfExists(oldImagePath);
            }
            String fileName = saveImage(image);
            supplierDto.setImage(fileName);
        } else {
            // If no new image uploaded, keep old image name
            supplierDto.setImage(current.getImage());
        }

        supplierRepository.updateSupplier(supplierDto);
        return new ResultMsg<SupplierDto>().success(supplierDto);
    }

    private String saveImage(MultipartFile image) throws IOException {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String extension = "";

        String originalFilename = image.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String fileName = UUID.randomUUID().toString() + extension;
        Path targetPath = Paths.get(uploadDir).resolve(fileName);
        Files.copy(image.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    private LocalDate readLocalDate(Cell cell, DataFormatter fmt) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case NUMERIC:  // true Excel date
                return cell.getLocalDateTimeCellValue().toLocalDate();
            case STRING:   // text like 1990?01?01
                String txt = fmt.formatCellValue(cell).trim();
                if (txt.isEmpty()) return null;

                // Replace non-breaking hyphen, en dash, and any other non-standard dash
                txt = txt.replace('\u2011', '-')  // Non-breaking hyphen (U+2011)
                        .replace('\u2013', '-')  // En dash (U+2013)
                        .replace('?', '-')      // If '?' is used
                        .replace('?', '-')      // Non-breaking hyphen (U+2011)
                        .replace(" ", "");      // Remove any spaces that might cause issues

                try {
                    // Log for debugging
                    System.out.println("Formatted date string: " + txt);

                    // Attempt to parse the date string
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    return LocalDate.parse(txt, formatter);
                } catch (DateTimeParseException e) {
                    // Log the error for debugging
                    System.err.println("Error parsing date: " + txt);
                    return null;
                }
            default:
                return null;
        }
    }

    @Override
    public ResultMsg<List<SupplierDto>> importSuppliers(MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            List<SupplierDto> suppliers = new ArrayList<>();

            DataFormatter fmt = new DataFormatter();
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row r = sheet.getRow(i);

                LocalDate birthday = readLocalDate(r.getCell(3), fmt);   // Date reading with error handling

                SupplierDto u = new SupplierDto(
                        fmt.formatCellValue(r.getCell(0)),
                        fmt.formatCellValue(r.getCell(1)),
                        fmt.formatCellValue(r.getCell(2)),
                        birthday,
                        fmt.formatCellValue(r.getCell(4)),
                        fmt.formatCellValue(r.getCell(5)),
                        fmt.formatCellValue(r.getCell(6)),
                        fmt.formatCellValue(r.getCell(7)),
                        fmt.formatCellValue(r.getCell(8)),
                        fmt.formatCellValue(r.getCell(9)),
                        fmt.formatCellValue(r.getCell(10)),
                        fmt.formatCellValue(r.getCell(11))
                );
                suppliers.add(u);
            }

            if (!CollectionUtils.isEmpty(suppliers)) {
                supplierRepository.importSuppliers(suppliers);
                return new ResultMsg<List<SupplierDto>>().success(suppliers,
                        String.format("Successfully imported %d suppliers", suppliers.size(),
                                suppliers.size() > 1 ? "d" : ""));
            } else {
                return new ResultMsg<List<SupplierDto>>().failure("Input at least 1 supplier");
            }
        } catch (IOException e) {
            return new ResultMsg<List<SupplierDto>>().failure("System exception");
        }
    }

    @Override
    public void exportSuppliers(OutputStream output, Map<String, Object> params) throws IOException {
        List<SupplierDto> suppliers = supplierRepository.getSuppliersOverall(params);
        if (CollectionUtils.isEmpty(suppliers)) {
            return;
        }
        Workbook workbook = new XSSFWorkbook(getClass().getResourceAsStream("/templates/Suppliers_Export.xlsx"));
        Sheet sheet = workbook.getSheetAt(0); // Assuming data goes into the first sheet
        int rowIdx = 1;
        for (SupplierDto u : suppliers) {
            Row r = sheet.createRow(rowIdx++);
            r.createCell(0).setCellValue(u.getLastName());
            r.createCell(1).setCellValue(u.getFirstName());
            r.createCell(2).setCellValue(u.getMiddleName());
            r.createCell(3).setCellValue(u.getBirthday() != null ? u.getBirthday().toString() : "");
            r.createCell(4).setCellValue(u.getAddress());
            r.createCell(5).setCellValue(u.getMobileNumber());
            r.createCell(6).setCellValue(u.getTelephoneNumber());
            r.createCell(7).setCellValue(u.getEmail());
            r.createCell(8).setCellValue(u.getFacebook());
            r.createCell(9).setCellValue(u.getInstagram());
            r.createCell(11).setCellValue(u.getMotherName());
            r.createCell(12).setCellValue(u.getFatherName());
        }
        workbook.write(output);
        workbook.close();
    }

    @Override
    public ResultPage<SupplierDto> getSupplierList(Map<String, Object> params) {
        int start = MapUtils.getInteger(params, "start", 0);
        int limit = MapUtils.getInteger(params, "limit", 10);
        List<SupplierDto> suppliers = supplierRepository.getSuppliers(params);
        int total = start == 1 && suppliers.size() < limit
                ? suppliers.size() : supplierRepository.getTotalSuppliersCount(params);
        return new ResultPage<>(suppliers, total, limit, start);
    }

    @Override
    public int deleteSupplier(Integer id) {
        return supplierRepository.deleteSupplier(id);
    }

    @Override
    public int deleteSuppliers(List<Integer> ids) {
        return supplierRepository.deleteSuppliers(ids);
    }
}
