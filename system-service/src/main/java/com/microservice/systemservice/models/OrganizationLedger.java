package com.microservice.systemservice.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.OneToMany;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationLedger implements Comparable<OrganizationLedger>{
    private int id;
    private int parentId;
    private String name;
    private int type;
    private String path;
    private String createTime;

    private int sort;

    @OneToMany
    //private Set<Organization> subOrgs;
    private List<OrganizationLedger> subOrgs;

    @Override
    public int compareTo(OrganizationLedger o) {
        return Integer.parseInt(String.valueOf((this.getSort()-o.getSort())));
    }
}
