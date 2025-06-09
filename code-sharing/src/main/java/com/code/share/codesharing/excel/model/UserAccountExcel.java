package com.code.share.codesharing.excel.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAccountExcel {

    private String accountName;
    private String fullName;
    private String gender;
    private String homeAddress;
    private String mobileNumber;
    //private String fiberhomeId;
    private String email;
    private String nationality;

    private String password;

    private String organizationId;
    private String organizationName;
    private String roleId;
    private String roleName;
    private String genderId;
    private String nationalityId;

    private String sourceMenu;
    private String userType;

    private String NEW_ID;

}