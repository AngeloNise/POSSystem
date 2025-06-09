package com.microservice.systemservice.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor(onConstructor_={@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)})
@JsonIdentityInfo(scope=Roles.class, generator= ObjectIdGenerators.IntSequenceGenerator.class)
public class Roles {

    private Long rolesId;
    private String fhId;
    private String permission;
    private String original_area;
    private String original_region;
    private String area;
    private String region;
    private String userAccess;
    private String privileges;
}
