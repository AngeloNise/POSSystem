package com.fiberhome.authservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class JwtResponse {
    private final String type = "Bearer ";
    private String id;
    private String username;
    private List<String> roles;
    private String accessToken;
    private boolean isAuthenticated;
    private List<OrganizationResponseDTO> organizationTree;
    private String assignedOrganizationId;

    private List<OrganizationResponseDTO> organizationLedgerTree;
    private List<OrganizationResponseDTO> organizationLedgerAreaTree;

    private List<Map> organizationGrade1;

    private List<Map> organizationGrade2;

    private List<Map> organizationGrade3;

    private List<Map> organizationGrade4;

    private Map userDetailsLog;
}

