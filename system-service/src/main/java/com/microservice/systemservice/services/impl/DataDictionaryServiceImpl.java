package com.microservice.systemservice.services.impl;

import com.google.gson.Gson;
import com.microservice.systemservice.models.DataDictionary;
import com.microservice.systemservice.repository.DataDictionaryRepository;
import com.microservice.systemservice.services.DataDictionaryService;
import com.microservice.systemservice.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataDictionaryServiceImpl implements DataDictionaryService {

    @Autowired
    DataDictionaryRepository dataDRepository;

    @Override
    public List<DataDictionary> getTEDDList(Map<String, Object> param) {
        log.info("getTEDDList: " + new Gson().toJson(param));
        return dataDRepository.getTEDDList(param);

    }

    public int getTEDDListCount(Map<String, Object> param){
        return dataDRepository.getTEDDListCount(param);
    }


    public void specificationCreate(Map<String, Object> body){
        String createdDate = MapUtils.getString(body, "createdDate");
        String updatedDate = MapUtils.getString(body, "updatedDate");

        body.put("createdDate", DateUtils.changeFormat(createdDate, DateUtils.YYYY_MM_DD_T_HH_MM_SS_Z,
                DateUtils.YYYY_MM_DD_HH_MM_SS));
        body.put("updatedDate", DateUtils.changeFormat(updatedDate, DateUtils.YYYY_MM_DD_T_HH_MM_SS_Z,
                DateUtils.YYYY_MM_DD_HH_MM_SS));

        dataDRepository.specificationCreate(body);
    }

    public void specificationUpdate(Map<String, Object> bodyEdit) {
        String updatedDate = MapUtils.getString(bodyEdit, "updatedDate");
        bodyEdit.put("updatedDate", DateUtils.changeFormat(updatedDate, DateUtils.YYYY_MM_DD_T_HH_MM_SS_Z,
                DateUtils.YYYY_MM_DD_HH_MM_SS));

        dataDRepository.specificationUpdate(bodyEdit);
    }

    @Override
    public List<DataDictionary> getAttributedList(Map<String, Object> param) {
        return dataDRepository.getAttributedList(param);

    }

    public int getAttributedListCount(Map<String, Object> param){
        return dataDRepository.getAttributedListCount(param);

    }
}
