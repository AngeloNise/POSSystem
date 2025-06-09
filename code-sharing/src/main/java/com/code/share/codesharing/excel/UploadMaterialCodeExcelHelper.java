package com.code.share.codesharing.excel;

import com.code.share.codesharing.excel.base.BaseExcelHelper;
import com.code.share.codesharing.excel.model.ResultMsg;
import com.code.share.codesharing.excel.model.TELedgerMaterialCodeExcel;
import com.code.share.codesharing.excel.model.UserAccountExcel;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;

import java.util.Map;

public class UploadMaterialCodeExcelHelper extends BaseExcelHelper<TELedgerMaterialCodeExcel> {

    public UploadMaterialCodeExcelHelper() {
        super(TELedgerMaterialCodeExcel.class);
    }



    @Override
    public Map<String, Integer> fieldCellMap() {
        return Map.ofEntries(
                Map.entry("materialsCode",0)
        );
    }

    @Override
    public ResultMsg<Boolean> validate(Workbook workbook) {
        ResultMsg<Boolean> main = super.validate(workbook);
        if (main.isSuccess() && main.getData()) {
            StringBuilder sb = new StringBuilder();

            DataFormatter dataFormatter = new DataFormatter();
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                /*String price = dataFormatter.formatCellValue(sheet.getRow(i).getCell(8));
                if (!StringUtils.isNumeric(price)) {
                    String column = CellReference.convertNumToColString(8);
                    main.setData(false);
                    sb.append(String.format("Row %s column %s [Original Price] should be numeric\n", i+1, column));
                }*/
            }
            main.setMessage(sb.toString());

        }
        return main;
    }
}
