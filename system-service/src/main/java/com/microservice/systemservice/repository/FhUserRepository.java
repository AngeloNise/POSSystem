package com.microservice.systemservice.repository;

import com.microservice.systemservice.models.FhUser;
import com.microservice.systemservice.models.Projects;
import com.microservice.systemservice.models.Roles;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface FhUserRepository {
    List<FhUser> loadAllUsers();

    int saveUserInput(FhUser userProfileDto);

    int saveUserRole(Roles userProfileDto);

    List<Projects> loadAllProjects();

    List<Projects> loadAllRegionAreaByProjectName(@Param("projectName") String projectName);

    List<FhUser> searchFhUsers(Map<String, Object> filterMap);

    int updateUserData(@Param("id") String id, FhUser fhUser);

    int updateUserRole(@Param("rolesId") String rolesId, Roles role);

    List<Roles> loadUserAccess(@Param("fhId") String fhId);

    String getFhIdUser(@Param("id") Long id);

    void dumpFhUser(@Param("id") String id);

    int insertUserRoleAccess(@Param("fhId") String id, Roles userProfileDto);

    void deleteUserAccess(@Param("Id") Long id);
}
