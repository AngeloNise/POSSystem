package com.microservice.systemservice.dto;


import com.microservice.systemservice.models.Roles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FhUserResponse {
    private String username;
    private String department;
    private String position;
    private String display_name;
    private String fhId;

    private List<Roles> roles;

    private boolean success;
    private String message;
}
