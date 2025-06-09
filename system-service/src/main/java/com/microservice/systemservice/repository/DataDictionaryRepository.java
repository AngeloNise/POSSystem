package com.microservice.systemservice.repository;

import com.microservice.systemservice.models.DataDictionary;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface DataDictionaryRepository {

    List<DataDictionary> getTEDDList(Map<String, Object> param);

    int getTEDDListCount(Map<String, Object> param);

    List<Map<String, Object>> getDDListFilterValues();

    void specificationCreate(Map<String, Object> body);

    void specificationUpdate(Map<String, Object> bodyEdit);

    void specificationDelete(Map<String, Object> bodyDelete);



    List<DataDictionary> getAttributedList(Map<String, Object> param);

    int getAttributedListCount(Map<String, Object> param);

    List<Map> getCodeNames();

    List<Map> getData();

    void addForm(Map data);

    void editForm(Map data);

    List<Map> showCategoryTree();

    List<Map> getCategoryChild();

    void addAttForm(Map<String, Object> body);

    void editAttForm(Map<String, Object> body);
}
