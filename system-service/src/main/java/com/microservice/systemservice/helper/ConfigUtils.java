package com.microservice.systemservice.helper;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class ConfigUtils {
    public static Map map = new HashMap();
    public static String getValue(String p_key) {
        String value = "";
        if(map.containsKey(p_key)) {
            value = (String)map.get(p_key);
        }else{
            try {
                value = PropertiesUtils.getPropertyValue(p_key);
            } catch (Exception e) {
                log.error("get props error:",e);
            }
            map.put(p_key, value);
        }
        return value;
    }
    public static void initCacheValues() throws Exception{
        Properties _p = PropertiesUtils.loadProperty();
        for(Iterator it = _p.entrySet().iterator(); it.hasNext();){
            Map.Entry _entry = (Map.Entry)it.next();
            map.put(_entry.getKey(),_entry.getValue());
        }
    }
    //
    public static void store(String p_key,String p_value) throws Exception{
        PropertiesUtils.writeProperties(p_key, p_value);
    }
}
