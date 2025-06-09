package com.microservice.systemservice.models;

import java.util.List;

public class OrgClass{
    public String key;
    public String label;
    public List<OrgClass> children;

    public OrgClass() {
    }

    public OrgClass(String key, String label) {
        this.key = key;
        this.label = label;
    }

    public OrgClass(String key, String label, List<OrgClass> children) {
        this.key = key;
        this.label = label;
        this.children = children;
    }
}
