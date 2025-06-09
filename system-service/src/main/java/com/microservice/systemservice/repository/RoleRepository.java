package com.microservice.systemservice.repository;

import com.microservice.systemservice.models.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mapper
@Repository
public interface RoleRepository {

    String GetRolePowerUserID(String power);

    String getUserRole(String accountId);

    void DeleteResourceByUserId(String roleId, String userId);
    void DeleteResourceByUserIdV2(String roleId, String userId, String companyId);

    void InsertNewResourceByUser(Map map4);

    List<Map> showAllResources();

    void saveNewResources(Map map);

    void DeleteAcctInRole(Map map3);

    void saveNewRole(Map map);

    void updateRoleById(Map map);

    List<Map> showAllRoles();

    List<Map> getResourceRoleAccount();
    List<Map> getResourceRoleAccountV2(String companyId);

    List<Map> showAllAccounts();
    List<Map> showAllAccountsV2(String companyId);

    List<Map> getUserWidgetPermissionData();
    List<Map> getUserWidgetPermissionDataV2(String companyId);

    void DeleteResourceByRoleId(@Param("roleId") String roleId,
                                @Param("companyId") String companyId);

    void InsertNewResourceByRole(Map map4);

    void UpdateRoleStateById(Map map);

    void SaveNewAcctInRole(Map map4);

    void updateResourceByRow(Map mapRes);

    void truncateFirstBeforeInsert(Map widgeMap);

    void AddNewWidgePermByUser(Map widgeMap);

    List<Map> getRoleWidgetPermissionData();
    List<Map> getRoleWidgetPermissionDataV2(String companyId);

    Map getButtonPermissionLegend();

    List<Map> getWidgeLegend();

}
