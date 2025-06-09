package com.microservice.systemservice.models;

import java.util.List;
import java.util.Map;

public class GetRequest {

    public enum typeReg {
        LISTMAP,
        STRING
    }

    public String name;
    public String remarks;
    public Object obj;
    public String type;
    public List<Map> mapList;

    public GetRequest() {
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public List<Map> getMapList() {
        return mapList;
    }

    public void setMapList(List<Map> mapList) {
        this.mapList = mapList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
