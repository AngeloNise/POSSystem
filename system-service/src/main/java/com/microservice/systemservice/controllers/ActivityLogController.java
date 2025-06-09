package com.microservice.systemservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.microservice.systemservice.services.ActivityLogService;
import com.microservice.systemservice.dto.ActivityLogDTO;

@RestController
public class ActivityLogController {

    @Autowired
    private ActivityLogService activityLogService;

    @PostMapping("/activity.log")
    public ResponseEntity<Void> logActivity(@RequestBody ActivityLogDTO activityLogDTO) {
        activityLogService.logActivity(activityLogDTO.getPageName(), activityLogDTO.getAction(), activityLogDTO.getCreatedByName());
        return ResponseEntity.ok().build();
    }
}
