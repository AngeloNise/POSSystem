package com.microservice.systemservice.services.impl;

import com.microservice.systemservice.models.Categories;
import com.microservice.systemservice.repository.CategoryRepository;
import com.microservice.systemservice.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("categoryService")
@RequiredArgsConstructor
public class CategoryServiceImp implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Categories> queryCategoriesAllList() {
        return categoryRepository.queryCategoriesAllList();
    }

    @Override
    public List<Categories> queryCategoriesList() {

        return categoryRepository.queryCategoriesList();
    }

    @Override
    public List<Categories> queryToolsAndEquipmentList() {

        return categoryRepository.queryToolsAndEquipmentList();
    }

    @Override
    public List<Categories> queryBrandAndModelList() {
        return categoryRepository.queryBrandAndModelList();
    }

    public List<Categories> queryModelList() {
        return categoryRepository.queryModelList();
    }

    @Override
    public List<Categories> queryLVTList() {

        return categoryRepository.queryLVTList();
    }


}
