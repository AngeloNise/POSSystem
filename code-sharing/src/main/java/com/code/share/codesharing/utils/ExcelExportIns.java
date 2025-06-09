package com.code.share.codesharing.utils;

import lombok.SneakyThrows;
import org.apache.commons.collections4.MapUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class ExcelExportIns {
    private static ExcelExportIns single_instance = null;

    InputStream templateScreen = null;
    Sheet sheet = null;
    int rowNum = 1;
    List<Map<String, Object>> ledgerItems = null;

    String[] columns = null;

    public ExcelExportIns() {
    }

    public static synchronized ExcelExportIns getInstance(){
        if(single_instance == null){
            single_instance = new ExcelExportIns();
        }
        return single_instance;
    }

    @SneakyThrows
    public ExcelExportIns SetParams(String resourcePath, List<Map<String,Object>> requestParams) {
        templateScreen = getClass().getClassLoader().getResourceAsStream(resourcePath);

/*        Workbook workbook =  WorkbookFactory.create(templateScreen);
        sheet = workbook.getSheetAt(0);*/

        ledgerItems = requestParams;

        return getInstance();
    }

    public ExcelExportIns SetColumns(String... val){
        columns = val;
        return getInstance();
    }

    @SneakyThrows
    public ByteArrayOutputStream build(){

        Workbook workbook =  WorkbookFactory.create(templateScreen);
        Sheet sheet = workbook.getSheetAt(0);

        for(Map map : ledgerItems) {
            Row row = sheet.createRow(rowNum++);

            /*row.createCell(0).setCellValue(rowNum - 1);*/

            for(int ii=0; ii < columns.length; ii++){

                if(ii == 0){
                    row.createCell(ii).setCellValue(rowNum - 1);
                } else {
                    row.createCell(ii).setCellValue(MapUtils.getString(map, columns[ii]));
                }

            }
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        workbook.write(stream);

        // reset
        rowNum = 1;
        workbook.close();
        templateScreen.close();

        return stream;
    }

}
