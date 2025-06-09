package com.microservice.systemservice.dto;

import com.microservice.systemservice.models.Organization;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.OneToMany;
import java.util.List;
import java.util.Set;

/**
 * @author Angel
 * @created 30/09/2022 - 9:23 AM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrganizationResponseDTO {

   // private Long organizationId;
    //private Long parentId;
    //private String fullNameOrg;
   // private Set<OrganizationResponseDTO> subOrgs;
    private String key;
    private String label;
    @OneToMany
    private List<OrganizationResponseDTO> children;
}
