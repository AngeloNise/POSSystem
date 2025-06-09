package com.code.share.codesharing.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TypeUtils {

    public static List<Map<String, Object>> jsonListToListMap(String json) {
        if (StringUtils.isNotBlank(json)) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<List<Map<String, Object>>>() {}.getType();
                return gson.fromJson(json, type);
            } catch (JsonSyntaxException e) {
                return new ArrayList<>();
            }
        } else {
            return new ArrayList<>();
        }
    }

}
