package com.code.share.codesharing.excel.base;


import com.code.share.codesharing.excel.model.ResultMsg;

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

    ResultMsg<Boolean> validate();

    void close();

    void clear();
}
