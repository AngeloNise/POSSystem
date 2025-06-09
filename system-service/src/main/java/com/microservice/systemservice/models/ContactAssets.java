package com.microservice.systemservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactAssets {

    private Long id;
    private String project;
    private String region;
    private String area;
    private String projectLeadName;
    private String projectLeadWeChatId;
    private String platformManagerAndAdminManager;
    private String pmWeChatId;
    private String areaAdmin;
    private String areaAdminWeChatId;
    private String contact;
    private Long groupId;
}
