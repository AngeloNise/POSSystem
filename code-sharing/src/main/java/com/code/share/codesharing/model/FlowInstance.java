package com.code.share.codesharing.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class FlowInstance {
    private Integer id;
    private String createBy;
    private String createByName;
    private String createByEmail;
    private LocalDateTime createTime;
    private String createTimeStr;
    private String updateBy;
    private LocalDateTime updateTime;

    private Integer defId;
    private String defKey;
    private String bizId;
    private String subject;
    private String createOrgId;
    private String createOrgPath;;
    private String createOrgType;
    private String status;
    private LocalDateTime endTime;
    private String duration;
    private Integer suspend;
    private String settings;
    private Integer mobile;

    private String defName;
    private String taskId;
    private String currentTaskApprover;
    private String currentTaskApproverNode;
    private String createOrgName;

    private String regionId;
    private String regionName;
    private String areaId;
    private String areaName;
    private String projectId;
    private String projectName;

    public void parseCreateTimeStr() {
        if (createTime != null) {
            this.createTimeStr = this.createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }

}
