package com.microservice.systemservice.repository;

import com.microservice.systemservice.models.Organization;
import com.microservice.systemservice.models.OrganizationLedger;
import com.microservice.systemservice.models.Projects;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
@Repository
public interface ProjectsRepository {
    List<Projects> loadAllProjects();

    List<Projects> loadAllRegionAreaByProjectName(@Param("project") String projectName);

    int saveNewProjectDetails(Map<String,Object> projs);

    void editProjectDetails(Projects projs);

    void deleteProjectDetails(@Param("projId") String projectId);

    Set<Organization> getOrganizations();

    List<Map<String, Object>> getAllRegions();

    //Set<OrganizationLedger> getLedgerOrganizations();
    List<Map> getLedgerOrganizations();

    List<Map> getLedgerAreaOrganizations();

    List<Map> getOrganizationGrade1();

    List<Map> getOrganizationGrade2();

    List<Map> getOrganizationGrade3();

    List<Map> getOrganizationGrade4();
}
