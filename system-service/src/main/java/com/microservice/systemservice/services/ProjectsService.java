package com.microservice.systemservice.services;

import com.code.share.codesharing.tools.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.systemservice.dto.OrganizationResponseDTO;
import com.microservice.systemservice.dto.ProjectsResponse;
import com.microservice.systemservice.models.Organization;
import com.microservice.systemservice.models.OrganizationLedger;
import com.microservice.systemservice.models.Projects;
import com.microservice.systemservice.repository.ProjectsRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectsService {

    private final ProjectsRepository projectsRepository;

    public List<Projects> getAllProjectList() {
        List<Projects> projects = projectsRepository.loadAllProjects();

        return projects.stream().sorted(Comparator.comparing(Projects::getProject))
                .collect(Collectors.toList());
       // return projects.stream().map(this::mapProjectsToProjectResponseDTO).collect(Collectors.toList());
    }
    private ProjectsResponse mapProjectsToProjectResponseDTO(Projects projects) {

        List<Projects> byAreas = projectsRepository.loadAllRegionAreaByProjectName(projects.getProject());

        return ProjectsResponse.builder()
                .project(projects.getProject())
                .area(byAreas.stream().filter(obj -> StringUtils.isNotEmpty(obj.getArea())).map(e-> Projects.builder().id(e.getId()).area(e.getArea()).build()).collect(Collectors.toList()))
                .region(byAreas.stream().filter(obj -> StringUtils.isNotEmpty(obj.getRegion())).map(e-> Projects.builder().id(e.getId()).region(e.getRegion()).build()).collect(Collectors.toList()))
                .build();
    }

    public int saveNewProjectDetails(Map<String,Object> projs) {
        int insert = projectsRepository.saveNewProjectDetails(projs);
        return insert;
    }

    public void editProjectDetails(Projects projs) {
       projectsRepository.editProjectDetails(projs);

    }

    public void deleteProjectDetails(String projectId) {
     projectsRepository.deleteProjectDetails(projectId);

    }

    public List<OrganizationResponseDTO> getOrganizationHierarchy() {
        Set<Organization> orgHierarchy = projectsRepository.getOrganizations();
        return new ArrayList<>(createOrganizationTree(orgHierarchy));
    }

    public List<OrganizationResponseDTO> getLedgerOrganizationHierarchy() {

       /* Set<OrganizationLedger> orgHierarchy = projectsRepository.getLedgerOrganizations();
        return new ArrayList<>(createLedgerOrganizationTree(orgHierarchy));*/

        List<Map> orgHierarchy = projectsRepository.getLedgerOrganizations();

        List<OrganizationResponseDTO> OrgLedgerList = new ArrayList<>();

        for(int i =0; i < orgHierarchy.size(); i++){
            //
            Map newMap = Utils.xo().conv().JsonStrToMap(orgHierarchy.get(i).get("json_res").toString());

            final ObjectMapper mapper = new ObjectMapper();
            final OrganizationResponseDTO orgLedger = mapper.convertValue(newMap, OrganizationResponseDTO.class);

            OrgLedgerList.add(orgLedger);
            //
        }

        return OrgLedgerList;
    }

    public List<OrganizationResponseDTO> getLedgerOrganizationAreaHierarchy() {

        List<OrganizationResponseDTO> OrgLedgerList = new ArrayList<>();

        List<Map> areaHierarchy = projectsRepository.getLedgerAreaOrganizations();

        for(int i =0; i < areaHierarchy.size(); i++){
            Map newMap = Utils.xo().conv().JsonStrToMap(areaHierarchy.get(i).get("json_res").toString());

            final ObjectMapper mapper = new ObjectMapper();
            final OrganizationResponseDTO orgLedger = mapper.convertValue(newMap, OrganizationResponseDTO.class);

            OrgLedgerList.add(orgLedger);
        }

        return OrgLedgerList;
    }

    private LinkedHashSet<OrganizationResponseDTO> createOrganizationTree(Set<Organization> orgHierarchy) {
        TreeSet<Organization> parentOrg = new TreeSet<>();
        Set<Organization> childOrg = new HashSet<>();

        for(Organization organization: orgHierarchy){
            if(organization.getGrade() == 1) {
                parentOrg.add(organization);
            } else {
                childOrg.add(organization);
            }
        }

        for(Organization parent: parentOrg) {
            addChildOrganization(parent,childOrg);
        }

        return parentOrg.stream().map(this::mapOrganizationTreeToDTO).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private LinkedHashSet<OrganizationResponseDTO>  createLedgerOrganizationTree(Set<OrganizationLedger> orgHierarchy){

        TreeSet<OrganizationLedger> parentOrg = new TreeSet<>();
        Set<OrganizationLedger> childOrg = new HashSet<>();

        for(OrganizationLedger organization: orgHierarchy){
            if(organization.getType() == 2) {
                parentOrg.add(organization);
            } else if(organization.getType() == 4) {
                childOrg.add(organization);
            }
        }

        for(OrganizationLedger parent: parentOrg) {
            addChildLedgerOrganization(parent,childOrg);
        }

        return null;
    }

    private void addChildOrganization(Organization parentOrg, Set<Organization> childOrg) {
        List<Organization> organizations = new ArrayList<>();

        for(Organization o: childOrg){
            if(Objects.nonNull(o.getParentId())){
                if (!Objects.deepEquals(parentOrg.getOrganizationId(),o.getParentId())) {
                    continue;
                }
                organizations.add(o);
            }
        }

        if (organizations.isEmpty()) {
            return;
        }
        parentOrg.setSubOrgs(organizations);
        for(Organization orgs: parentOrg.getSubOrgs()) {
            addChildOrganization(orgs,childOrg);
        }
    }
    private void addChildLedgerOrganization(OrganizationLedger parentOrg, Set<OrganizationLedger> childOrg) {
        List<OrganizationLedger> organizations = new ArrayList<>();

        for(OrganizationLedger o: childOrg){
            if(Objects.nonNull(o.getParentId())){
                if (!Objects.deepEquals(parentOrg.getId() ,o.getParentId())) {
                    continue;
                }
                organizations.add(o);
            }
        }

        if (organizations.isEmpty()) {
            return;
        }
        parentOrg.setSubOrgs(organizations);
        for(OrganizationLedger orgs: parentOrg.getSubOrgs()) {
            addChildLedgerOrganization(orgs,childOrg);
        }
    }

    private OrganizationResponseDTO mapOrganizationTreeToDTO(Organization organization){
        return OrganizationResponseDTO.builder()
                .key(String.valueOf(organization.getOrganizationId()))
                 //.parentId(organization.getParentId())
                .label(organization.getFullNameOrg())
                .children(organization.getSubOrgs() != null ? organization.getSubOrgs().stream().map(this::mapOrganizationTreeToDTO).collect(Collectors.toCollection(ArrayList::new)):null)
                .build();
    }

}
