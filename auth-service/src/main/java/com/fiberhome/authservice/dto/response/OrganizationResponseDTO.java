package com.fiberhome.authservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.OneToMany;
import java.util.List;

/**
 * @author Angel
 * @created 30/09/2022 - 9:23 AM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrganizationResponseDTO {

    //private Long organizationId;
    private String key;
    private String label;
    @OneToMany
    private List<OrganizationResponseDTO> children;
    //private Long parentId;
   // private String fullNameOrg;
    //private List<OrganizationResponseDTO> subOrgs;

}
