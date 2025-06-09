package com.microservice.systemservice.services;

import com.microservice.systemservice.models.DataDictionary;

import java.util.List;
import java.util.Map;

public interface DataDictionaryService {

    List<DataDictionary> getTEDDList(Map<String, Object> param);

    int getTEDDListCount(Map<String, Object> param);

    void specificationCreate(Map<String, Object> body);

    void specificationUpdate(Map<String, Object> bodyEdit);

    List<DataDictionary> getAttributedList(Map<String, Object> param);

    int getAttributedListCount(Map<String, Object> param);


}
