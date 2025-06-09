package com.microservice.systemservice.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Set;

/**
 * @author Angel
 * @created 30/09/2022 - 8:20 AM
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Organization implements Comparable<Organization> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long organizationId;
    private Long parentId;
    private String fullNameOrg;
    private int sort;
    private String telephoneNo;
    private int state;
    private String createdDate;
    private int grade;
    @OneToMany
    //private Set<Organization> subOrgs;
    private List<Organization> subOrgs;

    @Override
    public int compareTo(Organization o) {
        return Integer.parseInt(String.valueOf((this.getSort()-o.getSort())));
    }
}
