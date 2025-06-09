package com.microservice.systemservice.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class TaskNotif {
    private Integer id;
    private Integer instId;
    private Integer defId;
    private String subject;
    private LocalDateTime createTime;
    private String createTimeStr;
    private String name;

    public void formatCreateTime() {
        if (this.createTime != null) {
            try {
                setCreateTimeStr(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        .format(this.createTime));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
