package com.code.share.codesharing.excel.base;

import com.code.share.codesharing.excel.model.ResultMsg;
import com.google.gson.*;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
public abstract class BaseExcelHelper<T> implements ExcelHelper<T> {

    private Class<T> clazz;
    private FileInputStream fis;
    private Workbook workbook;
    private List<T> dataList;

    private Map<String, Integer> fieldCellMap; //Key: Variable name, Value: Column index

    public BaseExcelHelper(Class<T> clazz) {
        this.clazz = clazz;
        setFieldCellMap(fieldCellMap());
    }

    public BaseExcelHelper(Class<T> clazz, File file) {
        this.clazz = clazz;
        setFile(file);
        setFieldCellMap(fieldCellMap());
    }

    public void setFile(File file) {
        try {
            this.fis = new FileInputStream(file);
            this.workbook = new XSSFWorkbook(fis);
        } catch (IOException ignored) { }
    }

    public Map<String, Integer> getFieldCellMap() {
        return fieldCellMap;
    }

    public void setFieldCellMap(Map<String, Integer> fieldCellMap) {
        this.fieldCellMap = fieldCellMap;
    }

    /**
     * Convert the content of the Excel file to an object list
     * @return Contents of the Excel
     */
    @Override
    public List<T> read() {
        if (CollectionUtils.isEmpty(this.dataList)) {
            this.dataList = new ArrayList<>();
        }
        if (this.workbook.getNumberOfSheets() > 0) {
            DataFormatter dataFormatter = new DataFormatter();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

            Gson gson = new GsonBuilder().serializeNulls()
                    .registerTypeAdapter(LocalDate.class,
                            (JsonSerializer<LocalDate>) (localDate, type, context)
                                    -> new JsonPrimitive(localDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))))
                    .registerTypeAdapter(LocalDate.class,
                            (JsonDeserializer<LocalDate>) (jsonElement, type, context)
                                    -> LocalDate.parse(jsonElement.getAsString(), DateTimeFormatter.ofPattern("MM/dd/yyyy")))
                    .create();

            Sheet sheet = this.workbook.getSheetAt(0);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            for (int x = 1; x < sheet.getPhysicalNumberOfRows(); x++) {
                Map<String, Object> dataThis = new HashMap<>();
                for (Map.Entry<String, Integer> entry : this.fieldCellMap.entrySet()) {


                    /*if(cell != null) { // old codes
                        if(StringUtils.equalsIgnoreCase(entry.getKey(),"price") || StringUtils.equalsIgnoreCase(entry.getKey(),"remainingValue")){
                            BigDecimal currency = BigDecimal.valueOf(cell.getNumericCellValue()).setScale(2, RoundingMode.UP);
                            dataThis.put(entry.getKey(), currency);
                        }else if(StringUtils.equalsIgnoreCase(entry.getKey(),"datePurchased")){
                            String datePurchased = formatter.format(cell.getDateCellValue());
                            dataThis.put(entry.getKey(),datePurchased);
                        }else{
                            dataThis.put(entry.getKey(), dataFormatter.formatCellValue(cell,evaluator));
                        }
                    }*/
                    //

                    try {
                        Cell cell = sheet.getRow(x).getCell(entry.getValue());

                        if(cell != null) {
                            dataThis.put(entry.getKey(), dataFormatter.formatCellValue(cell,evaluator));
                        }
                    } catch (Exception e) {
                    }
                }
                this.dataList.add(gson.fromJson(gson.toJson(dataThis), clazz));
            }
        }
        return this.dataList;
    }

    @Override
    public Map<String, Object> readRaw() {
        Map<String, Object> rawData = new HashMap<>();
        List<Map<String, String>> fieldHeaderList = new ArrayList<>();
        List<Map<String, String>> rawDataList = new ArrayList<>();
        if (this.workbook.getNumberOfSheets() > 0) {
            DataFormatter dataFormatter = new DataFormatter();
            Sheet sheet = this.workbook.getSheetAt(0);
            for (int x = 0; x < sheet.getPhysicalNumberOfRows(); x++) {
                Row row = sheet.getRow(x);
                Map<String, String> dataThis = new HashMap<>();
                for (int y = 0; y < row.getPhysicalNumberOfCells(); y++) {
                    if (x == 0) {
                        fieldHeaderList.add(
                                Map.of("field", String.format("field%s", y)
                                        ,"header", dataFormatter.formatCellValue(row.getCell(y)))
                        );
                    } else {
                        dataThis.put(String.format("field%s", y), dataFormatter.formatCellValue(row.getCell(y)));
                    }
                }
                if (x > 0) rawDataList.add(dataThis);
            }
        }
        rawData.put("fieldHeaderList", fieldHeaderList);
        rawData.put("rawDataList", rawDataList);
        return rawData;
    }


    @Override
    public ResultMsg<Boolean> validate() {
        if (this.workbook == null) {
            return new ResultMsg<>(false, "No Excel file found",null);
        }
        return validate(this.workbook);

    }

    /**
     * Closes input stream and workbook. Always call after using Excel helper
     */
    @Override
    public void close() {
        try {
            if (this.fis != null) {
                this.fis.close();
            }
            if (this.workbook != null) {
                this.workbook.close();
            }
        } catch (IOException ignored) { }
    }

    /**
     * Empties most variables
     */
    @Override
    public void clear() {
        close();
        this.fis = null;
        this.workbook = null;
        if (!CollectionUtils.isEmpty(this.dataList)) {
            this.dataList.clear();
        }
    }

    /**
     * Default validator
     * VERSION 2: Checks if column count in template matches the uploaded file
     */
    public ResultMsg<Boolean> validate(Workbook workbook) {
        //VERSION 2 - MATCH EXCEL COLUMN COUNTS
        ResultMsg<Boolean> result = new ResultMsg<>(true,"success",true);
        Sheet sheet = workbook.getSheetAt(0);
        try {
            if (sheet.getPhysicalNumberOfRows() < 1) {
                result.setData(false);
                result.setMessage("Empty file, check if the first sheet is not empty");
                return result;
            }

            Row row = sheet.getRow(0);
            int rowNum = row.getPhysicalNumberOfCells();
            int mapSize = getFieldCellMap().entrySet().size();
            if (mapSize != rowNum) {
                result.setData(false);
                result.setMessage("Number of columns does not match the template, please download the template");
                return result;
            }
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;

    }

}
