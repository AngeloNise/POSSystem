package com.microservice.systemservice.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FhUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fhId;
    private String projName;
    private String fullName;
    private String fhUsername;
    private String password;
    private String department;
    private String fhPosition;
    private String status;

    private String userAccess;

    private String area;

    private String region;
    @JsonFormat(shape= JsonFormat.Shape.OBJECT)
    private List<Roles> rolesList;
}


