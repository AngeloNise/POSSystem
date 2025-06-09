package com.microservice.systemservice.helper;

import com.microservice.systemservice.models.Categories;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;

import java.lang.reflect.Field;
import java.util.*;

@Slf4j
public class CategoriesUtil {

    public static List<Categories> AllCATEGORIES_LIST=new ArrayList<Categories>();

    public static List<Categories> CATEGORIES_LIST=new ArrayList<Categories>();

    public static List<Categories> TOOLSANDEQUIPMENT_LIST=new ArrayList<Categories>();

    public static List<Categories> BRANDANDMODEL_LIST=new ArrayList<Categories>();

    public static List<Categories> MODEL_LIST=new ArrayList<Categories>();

    public static List<Categories> LVT_LIST=new ArrayList<Categories>();

    public static Categories getCategory(String strCode,String strCategory){
        Categories category=null;
        for(Categories ctgry: CATEGORIES_LIST){
            if(Objects.equals(ctgry.getCode(), strCode)) {
                if(Objects.equals(ctgry.getCategoryName(), strCategory)) {
                    category=ctgry;
                }
            }
        }
        return category;
    }

    public static String getCategoryName(String strCode,String strCategory){
        String name = "";
        Categories ctrgy = getCategory(strCode,strCategory);
        if(ctrgy!=null){
            name = ctrgy.getCategoryName();
        }
        return name;
    }

    public static Categories getCategoriesById(String strCode,Integer categoryNum){
        Categories category=null;
        for(Categories ctgry: CATEGORIES_LIST){
            if(ctgry.getCode().equals(strCode)){
                if(ctgry.getCategoryId().equals(categoryNum)){
                    category=ctgry;
                }
            }
        }
        return category;
    }

    public static List<Categories> queryCategoryListByCode(String strCode){
        List<Categories> categoriesList=new ArrayList<Categories>();
        for(Categories ctgry: CATEGORIES_LIST){
            if(ctgry.getCode().equals(strCode)){
                categoriesList.add(ctgry);
            }
        }
        return categoriesList;
    }

    public static Integer getIdByCategoryNameAndCode(String strCode,String categoryName){
        Categories category=null;

        for(Categories ctgry: AllCATEGORIES_LIST){
            if(ctgry.getCode().equals(strCode) && ctgry.getCategoryName().equalsIgnoreCase(categoryName)){
                category=ctgry;
            }
        }
        return category==null?null:category.getCategoryId();
    }

    public static Integer getIntanceIdByCategoryNameAndCode(String strCode,String categoryName){
        Categories category=null;

        for(Categories ctgry: AllCATEGORIES_LIST){
            if(ctgry.getCode().equals(strCode) && ctgry.getCategoryName().equalsIgnoreCase(categoryName)){
                category=ctgry;
            }
        }
        return category==null?null:Integer.valueOf(category.getInstanceId());
    }

    public static Map isCategoryExist(String category,String toolsAndEquipment,String brand,String model, String specification,String itemCode){
        int checker = 1;
        Map data =new HashMap();
        String catId = "";
        String taeId = "";
        String bamId = "";
        String modId = "";
        String lvtId = "";
        data.put("result","error");

        for(Categories ctgry: CATEGORIES_LIST){
            if(ctgry.getCategoryName().equalsIgnoreCase(category)) {
                checker = 2;
                catId = ctgry.getCategoryId().toString();
                break;
            }
        }

        if(checker == 2){
            for(Categories tae: TOOLSANDEQUIPMENT_LIST){
                if(tae.getCategoryName().equalsIgnoreCase(toolsAndEquipment) && tae.getParentId().equalsIgnoreCase(catId)) {
                    checker = 3;
                    taeId = tae.getCategoryId().toString();
                    break;
                }
            }
        }

        if(checker == 3){
            for(Categories bam: BRANDANDMODEL_LIST){
                if(bam.getCategoryName().equalsIgnoreCase(brand) && bam.getParentId().equalsIgnoreCase(taeId)) {
                    checker = 4;
                    bamId = bam.getCategoryId().toString();
                    break;
                }
            }
        }

        if(checker == 4){
            for(Categories mod: MODEL_LIST){
                if(mod.getCategoryName().equals(model) && mod.getParentId().equalsIgnoreCase(bamId)) {
                    checker = 5;
                    modId = mod.getCategoryId().toString();
                    data.put("result","success");
                    break;
                }
            }
        }

        data.put("itemCode","error");
        for(Categories lvt: LVT_LIST){
            if(lvt.getCategoryName().equalsIgnoreCase(itemCode)) {
                lvtId = lvt.getCategoryId().toString();
                data.put("itemCode","success");

                break;
            }
        }

        data.put("catId",catId);
        data.put("taeId",taeId);
        data.put("bamId",bamId);
        data.put("modId",modId);
        data.put("lvtId",lvtId);
        data.put("resultType", checker);
        return data;
    }

    public static List<Categories> getCategoriesByCode(String strCode){
        List<Categories> categoriesList=new ArrayList<Categories>();
        for(Categories ctgry: AllCATEGORIES_LIST){
            if(ctgry.getCode().equals(strCode)){
                categoriesList.add(ctgry);
            }
        }
        return categoriesList;
    }

    /**
     * @param code - Optional
     * @param keyCol - The key of the map (Should be a property of Categories)
     * @param valueCol - The value of the map (Should be a property of Categories)
     */
    public static Map asMap(String code, String keyCol, String valueCol) {
        List<Categories> categories = code != null ? getCategoriesByCode(code) : AllCATEGORIES_LIST;
        Map map = new HashMap<>();
        for (Categories c : categories) {
            try {
                Field fieldKey = Categories.class.getDeclaredField(keyCol);
                fieldKey.setAccessible(true);
                Field fieldVal = Categories.class.getDeclaredField(valueCol);
                fieldVal.setAccessible(true);
                map.put(fieldKey.get(c), fieldVal.get(c));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}
