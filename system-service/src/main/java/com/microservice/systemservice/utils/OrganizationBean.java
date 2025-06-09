package com.microservice.systemservice.utils;

import com.microservice.systemservice.dto.OrganizationResponseDTO;
import com.microservice.systemservice.models.Organization;
import com.microservice.systemservice.services.ProjectsService;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Angel
 * @created 10/10/2022 - 12:37 PM
 */
@Component
public class OrganizationBean {

    private static ProjectsService projectsService;
    public OrganizationBean(ProjectsService projectsService) {
        OrganizationBean.projectsService = projectsService;
    }

    public Set<Organization> organizationHierarchy = new HashSet<>();
    private static volatile List<OrganizationResponseDTO> organizationTree = new ArrayList<>();
    private static volatile List<OrganizationResponseDTO> ledgerOrganizationTree = new ArrayList<>();

    private static volatile List<OrganizationResponseDTO> ledgerOrganizationAreaTree = new ArrayList<>();
    public static void init() {
        setOrganizationTree(projectsService.getOrganizationHierarchy());
        setLedgerOrganizationTree(projectsService.getLedgerOrganizationHierarchy());
        setLedgerAreaOrganizationTree(projectsService.getLedgerOrganizationAreaHierarchy());
    }
    public static List<OrganizationResponseDTO> getOrganizationTree() {
        return organizationTree;
    }

    public static List<OrganizationResponseDTO> getLedgerOrganizationTree() {
        return ledgerOrganizationTree;
    }

    public static List<OrganizationResponseDTO> getLedgerOrganizationAreaTree() {
        return ledgerOrganizationAreaTree;
    }

    public static void setOrganizationTree(List<OrganizationResponseDTO> organizationTree) {
        OrganizationBean.organizationTree = organizationTree;
    }
    public static void setLedgerOrganizationTree(List<OrganizationResponseDTO> organizationTree) {
        OrganizationBean.ledgerOrganizationTree = organizationTree;
    }
    public static void setLedgerAreaOrganizationTree(List<OrganizationResponseDTO> organizationTree) {
        OrganizationBean.ledgerOrganizationAreaTree = organizationTree;
    }
}
