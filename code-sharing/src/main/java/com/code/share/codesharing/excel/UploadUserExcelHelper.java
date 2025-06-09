package com.code.share.codesharing.excel;


import com.code.share.codesharing.excel.base.BaseExcelHelper;
import com.code.share.codesharing.excel.model.ResultMsg;
import com.code.share.codesharing.excel.model.UserAccountExcel;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;


import java.util.Map;

public class UploadUserExcelHelper extends BaseExcelHelper<UserAccountExcel> {

    public UploadUserExcelHelper() {
        super(UserAccountExcel.class);
    }

    @Override
    public Map<String, Integer> fieldCellMap() {
        return Map.ofEntries(
                Map.entry("accountName",0),
                Map.entry("fullName",1),
                Map.entry("gender",2),
                Map.entry("homeAddress",3),
                Map.entry("mobileNumber",4),

                //Map.entry("fiberhomeId",5),
                Map.entry("email",6),
                Map.entry("nationality",7),
                Map.entry("organizationName",8),
                Map.entry("roleName",9),

                Map.entry("genderId",10),
                Map.entry("nationalityId",11),
                Map.entry("organizationId",12),
                Map.entry("roleId",13),
                Map.entry("sourceMenu",14),

                Map.entry("userType",15)
        );
    }
    @Override
    public ResultMsg<Boolean> validate(Workbook workbook) {
        ResultMsg<Boolean> main = super.validate(workbook);
        if (main.isSuccess() && main.getData()) {
            StringBuilder sb = new StringBuilder();
            DataFormatter dataFormatter = new DataFormatter();
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1,sheetSize = sheet.getPhysicalNumberOfRows(); i < sheetSize; i++) {
                String category = dataFormatter.formatCellValue(sheet.getRow(i).getCell(0));
                /*if(!CategoriesUtil.isCategoryExist(category)){
                    String column = CellReference.convertNumToColString(0);
                    main.setData(false);
                    sb.append(String.format("Row %s column %s %s not found on the [Category] list %n", i+1, column,category));
                }*/

            }
            main.setMessage(sb.toString());
        }
        return main;
    }
}
