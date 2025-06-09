package com.fiberhome.authservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Angel
 * @created 05/12/2022 - 4:29 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrgUserRelation {
    private String userId;
    private String projectId;
    private String areaId;
}
