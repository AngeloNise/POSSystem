package com.microservice.systemservice.services;


import com.microservice.systemservice.dto.FhUserResponse;
import com.microservice.systemservice.dto.ProjectsResponse;
import com.microservice.systemservice.dto.RolesResponse;
import com.microservice.systemservice.models.FhUser;
import com.microservice.systemservice.models.Projects;
import com.microservice.systemservice.models.Roles;
import com.microservice.systemservice.repository.FhUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FhUserService {

    private final FhUserRepository fhUserRepository;


    public List<FhUser> getAllFhUsers() {

        return fhUserRepository.loadAllUsers();
    }

    public int saveUserInput(FhUser userProfileDto) {

        return fhUserRepository.saveUserInput(userProfileDto);
    }

    public int saveUserRole(FhUser userProfileDto) {
        int roles = 0;
        for(Roles ro: userProfileDto.getRolesList()){
            roles = fhUserRepository.saveUserRole(ro);
        }
        return roles;
    }

    public List<ProjectsResponse> loadUserProjects() {
        List<Projects> projects = fhUserRepository.loadAllProjects();

        return projects.stream().map(this::mapProjectsToProjectResponseDTO).collect(Collectors.toList());
    }

    private ProjectsResponse mapProjectsToProjectResponseDTO(Projects projects) {

        List<Projects> byAreas = fhUserRepository.loadAllRegionAreaByProjectName(projects.getProject());

        return ProjectsResponse.builder()
                .project(projects.getProject())
                .area(byAreas.stream().filter(obj -> StringUtils.isNotEmpty(obj.getArea())).map(e-> Projects.builder().id(e.getId()).area(e.getArea()).build()).collect(Collectors.toList()))
                .region(byAreas.stream().filter(obj -> StringUtils.isNotEmpty(obj.getRegion())).map(e-> Projects.builder().id(e.getId()).region(e.getRegion()).build()).collect(Collectors.toList()))
                .build();
    }

    public List<FhUser> searchFhUsers(String search) {
        Map<String, Object> filterMap = new HashMap<>();

        filterMap.put("access","");
        if(StringUtils.isNumeric(search)){
            filterMap.put("byFhId",search);
        }else{
            filterMap.put("byName",search);
        }

        List<FhUser> fhUserList = fhUserRepository.searchFhUsers(filterMap);

        return fhUserList;

    }

    public FhUserResponse updateUserData(String id, FhUser fhUser) {
        int fhUser1 = fhUserRepository.updateUserData(id,fhUser);

        return FhUserResponse.builder()
                .display_name(fhUser.getFullName())
                .username(fhUser.getFhUsername())
                .fhId(fhUser.getFhId())
                .message("Successfully edited!")
                .success(true)
                .department(fhUser.getDepartment())
                .position(fhUser.getFhPosition())
                .roles(fhUser.getRolesList()).build();
    }

    private RolesResponse mapRolesToRolesResponseDTO(Roles roles) {

        return RolesResponse.builder()
                .Id(roles.getRolesId())
                .fhid(roles.getFhId())
                .region(roles.getRegion())
                .access(roles.getUserAccess())
                .area(roles.getArea())
                .permission(roles.getPermission())
                .build();
    }

    private String getFhIdById(Long id){
        return fhUserRepository.getFhIdUser(id);
    }

    public List<RolesResponse> loadUserAccess(String fhId) {

        List<Roles> rolesList = fhUserRepository.loadUserAccess(fhId);

        return rolesList.stream().map(this::mapRolesToRolesResponseDTO).collect(Collectors.toList());

    }

    public void deleteFhUserById(String id) {
        try{
            fhUserRepository.dumpFhUser(id);
        }catch (Exception e){
            log.error(e.getMessage());
        }

    }

    public void deleteUserAccessById(Long id) {
        try{
            fhUserRepository.deleteUserAccess(id);
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

    public RolesResponse insertUserAccess(String id, Roles userProfileDto) {
        int role = fhUserRepository.saveUserRole(userProfileDto);

        return RolesResponse.builder()
                .Id(userProfileDto.getRolesId())
                .fhid(userProfileDto.getFhId())
                .region(userProfileDto.getRegion())
                .access(userProfileDto.getUserAccess())
                .area(userProfileDto.getArea())
                .permission(userProfileDto.getPermission())
                .success(true)
                .message("Successfully added!")
                .build();
    }

    public RolesResponse updateUserAccess(String rolesId, Roles role) {

         int roles = fhUserRepository.updateUserRole(rolesId, role);

        return RolesResponse.builder()
                .Id(role.getRolesId())
                .fhid(role.getFhId())
                .region(role.getRegion())
                .access(role.getUserAccess())
                .area(role.getArea())
                .permission(role.getPermission())
                .success(true)
                .message("Successfully updated!")
                .build();
    }
}
