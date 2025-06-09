package com.microservice.systemservice.helper.Excel;

import com.microservice.systemservice.helper.CategoriesUtil;
import com.microservice.systemservice.helper.Excel.base.BaseExcelHelper;
import com.microservice.systemservice.models.DataDictionary;
import com.microservice.systemservice.models.ResultMsg2;
import com.microservice.systemservice.models.UploadSpecification;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;

import java.util.HashMap;
import java.util.Map;

public class UploadSpecificationHelper extends BaseExcelHelper<DataDictionary> {

    public UploadSpecificationHelper(){
        super(DataDictionary.class);
    }

    @Override
    public Map<String, Integer> fieldCellMap() {
        return Map.ofEntries(
                Map.entry("category",0),
                Map.entry("teName",1),
                Map.entry("itemCode",2),
                Map.entry("brand",3),
                Map.entry("model",4),
                Map.entry("specification",5),
                Map.entry("description",6),
                Map.entry("marketValue",7),
                Map.entry("lifeSpan",8)
        );
    }

    @Override
    public ResultMsg2<Boolean> validate(Workbook workbook) {
        ResultMsg2<Boolean> result = new ResultMsg2<>(true,"success",true);
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

    public Map checker2(int i , DataDictionary ups) {
        Map data = new HashMap();
        Boolean result = true;
        StringBuilder sb = new StringBuilder();

        String category = ups.getCategory();
        String toolsAndEquipment = ups.getTeName();
        String brand = ups.getBrand();
        String model = ups.getModel();
        String specification = ups.getSpecification();
        String itemCode = ups.getItemCode();


        if(category == null || category.equals("")){
            sb.append(String.format("Row %s column A - Category is Required. %n", i + 1));
            result = false;
        }

        if (toolsAndEquipment == null || toolsAndEquipment.equals("")){
            sb.append(String.format("Row %s column D - Tools and Equipment Name is Required. %n", i + 1));
            result = false;
        }

        if (brand == null || brand.equals("")){
            sb.append(String.format("Row %s column E - Brand is Required. %n", i + 1));
            result = false;
        }

        if (model == null || model.equals("")){
            sb.append(String.format("Row %s column F - Model is Required. %n", i + 1));
            result = false;
        }

        Map checker  = CategoriesUtil.isCategoryExist(category,toolsAndEquipment,brand,model,specification,itemCode);
        if(MapUtils.getString(checker,"result").equals("error")) {
            int numToCol = 0;
            String catError = "";
            String catEntry = "";
            switch (MapUtils.getString(checker, "resultType")) {
                case "4":
                    numToCol = 5;
                    catError = "Model";
                    catEntry = model== null?"":model;
                    checker.put("modId","");
                    break;
                case "3":
                    numToCol = 4;
                    catError = "Brand";
                    catEntry = brand== null?"":brand;
                    checker.put("bamId","");
                    break;
                case "2":
                    numToCol = 3;
                    catError = "Tools and Equipment";
                    catEntry = toolsAndEquipment== null?"":toolsAndEquipment;
                    checker.put("taeId","");
                    break;
                default:
                    numToCol = 0;
                    catError = "Categories";
                    catEntry = category== null?"":category;
                    checker.put("catId","");
            }

            String column = CellReference.convertNumToColString(numToCol);
            //result = false;
            sb.append(String.format("Row %s column %s - %s not found on the [%s] list %n", i + 1, column, catEntry, catError));
        }
        // Checking on List LOA
        if(MapUtils.getString(checker,"itemCode").equals("error")){
            sb.append(String.format("Row %s column C - %s not found on the [Item Code] list %n", i + 1, itemCode));
            result = false;
        }

        String marketValue = String.valueOf(ups.getMarketValue());
        String lifeSpan = String.valueOf(ups.getLifeSpan());


        /*if (specification == null || specification.equals("")){
            sb.append(String.format("Row %s column F - Specification is Required. %n", i + 1));
            result = false;
        }*/

        /*if (ups.getDescription() == null || ups.getDescription().equals("")){
            sb.append(String.format("Row %s column G - Description is Required. %n", i + 1));
            result = false;
        }*/

        if(marketValue == null || marketValue.equals("")){
            sb.append(String.format("Row %s column H - Market Value is Required. %n", i + 1));
            result = false;
        }

        if(lifeSpan != null && lifeSpan.equals("")){
            sb.append(String.format("Row %s column I - Life Span is Required. %n", i + 1));
            result = false;
        }

        data.put("catId",MapUtils.getString(checker,"catId"));
        data.put("taeId",MapUtils.getString(checker,"taeId"));
        data.put("bamId",MapUtils.getString(checker,"bamId"));
        data.put("modId",MapUtils.getString(checker,"modId"));
        data.put("lvtId",MapUtils.getString(checker,"lvtId"));
        data.put("success",result);
        data.put("message",sb.toString());
        return data;
    }
}
