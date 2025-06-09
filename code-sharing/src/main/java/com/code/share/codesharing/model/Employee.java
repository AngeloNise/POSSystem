package com.code.share.codesharing.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Employee {
    public String name;
    public String email;
    public String contact;

    public String Output(){
        return "Hello my World";
    }
}
