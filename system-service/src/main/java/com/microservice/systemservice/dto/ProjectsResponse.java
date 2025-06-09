package com.microservice.systemservice.dto;

import com.microservice.systemservice.models.Projects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectsResponse {

    private String project;
    private List<Projects> region;
    private List<Projects> area;
}
