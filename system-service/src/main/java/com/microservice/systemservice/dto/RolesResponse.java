package com.microservice.systemservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolesResponse {
    private Long Id;
    private String fhid;
    private String permission;
    private String area;
    private String region;
    private String access;
    private boolean success;
    private String message;

}
