package com.microservice.systemservice.services;

import com.microservice.systemservice.models.Categories;

import java.util.List;
import java.util.Map;

public interface CategoryService {
    List<Categories> queryCategoriesAllList();
    List<Categories> queryCategoriesList();
    List<Categories> queryToolsAndEquipmentList();
    List<Categories> queryBrandAndModelList();
    List<Categories> queryModelList();
    List<Categories> queryLVTList();

}
