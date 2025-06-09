package com.microservice.systemservice.helper.Excel.base;


import com.microservice.systemservice.models.ResultMsg2;

import java.util.List;
import java.util.Map;

/**
 * @author Angel
 * @created 28/03/2023 - 3:13 PM
 */
public interface ExcelHelper<T> {

    Map<String, Integer> fieldCellMap();

    List<T> read();

    Map<String, Object> readRaw();

    ResultMsg2<Boolean> validate();

    void close();

    void clear();
}
