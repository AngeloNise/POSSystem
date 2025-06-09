package com.microservice.systemservice.repository;

import com.microservice.systemservice.models.Categories;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
@Mapper
@Repository
public interface CategoryRepository {
    List<Categories> queryCategoriesAllList();
    List<Categories> queryCategoriesList();
    List<Categories> queryToolsAndEquipmentList();
    List<Categories> queryBrandAndModelList();
    List<Categories> queryModelList();
    List<Categories> queryLVTList();

}
