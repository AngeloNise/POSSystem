package com.fiberhome.authservice.service;

import com.fiberhome.authservice.dto.response.OrganizationResponseDTO;
import com.fiberhome.authservice.model.OrgUserRelation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * @author Angel
 * @created 10/10/2022 - 2:18 PM
 */
@FeignClient(name = "system-service",value = "system-service")
@Service
public interface SystemService {

    @RequestMapping(value = "/projects/getOrganizationListTree")
    List<OrganizationResponseDTO> organizationListTree();

    @RequestMapping(value = "/projects/getLedgerOrganizationListTree")
    List<OrganizationResponseDTO> ledgerOrganizationListTree();

    @RequestMapping(value = "/projects/getLedgerOrganizationListAreaTree")
    List<OrganizationResponseDTO> ledgerOrganizationListAreaTree();

    @RequestMapping("/projects/gerUserInformation")
    String getUserInformation();

    @GetMapping(value = "/system/menu/getOrgUserRelation/{id}")
    List<OrgUserRelation> getUserOrgRelationByAccountId(@PathVariable("id") String accountId);


    @RequestMapping(value = "/projects/getOrganizationGrade1")
    List<Map> organizationGrade1();

    @RequestMapping(value = "/projects/getOrganizationGrade2")
    List<Map> organizationGrade2();

    @RequestMapping(value = "/projects/getOrganizationGrade3")
    List<Map> organizationGrade3();

    @RequestMapping(value = "/projects/getOrganizationGrade4")
    List<Map> organizationGrade4();
}
