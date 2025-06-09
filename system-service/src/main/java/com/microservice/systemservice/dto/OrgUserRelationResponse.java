package com.microservice.systemservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Angel
 * @created 05/12/2022 - 4:29 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrgUserRelationResponse {
    private String userId;
    private String projectId;
    private String areaId;
}
