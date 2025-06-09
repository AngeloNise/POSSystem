package com.microservice.systemservice.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetResponse {
    public String name;
    public String[] conditions;
    public String action;
    public String str;
    public Object object;

    public String userId;
    public String userFullName;
    public String companyId ;

    public GetResponse() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String[] getConditions() {
        return conditions;
    }

    public void setConditions(String[] conditions) {
        this.conditions = conditions;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserFullName() { return userFullName; }
    public void setUserFullName(String userFullName) { this.userFullName = userFullName; }
}
