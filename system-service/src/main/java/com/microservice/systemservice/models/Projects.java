package com.microservice.systemservice.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Projects {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String project;
    private String region;
    private String area;

    public String getRegion() {
        return StringUtils.defaultIfBlank(region,"");
    }
}
