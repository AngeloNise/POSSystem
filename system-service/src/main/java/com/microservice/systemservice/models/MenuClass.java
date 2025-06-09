package com.microservice.systemservice.models;

import java.util.List;

public class MenuClass {
    public String label;
    public String icon;
    public String to;

    public List<MenuClass> items;


    public MenuClass(String label, String icon, String to) {
        this.label = label;
        this.icon = icon;
        this.to = to;
    }

    public MenuClass(){

    }

    public MenuClass(String label, String icon, String to, List<MenuClass> items) {
        this.label = label;
        this.icon = icon;
        this.to = to;
        this.items = items;
    }
}
