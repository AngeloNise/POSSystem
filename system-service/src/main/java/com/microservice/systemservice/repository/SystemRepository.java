package com.microservice.systemservice.repository;

import com.microservice.systemservice.dto.OrgUserRelationResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SystemRepository {

    List<Map> findByRole(String role);
    List<Map> findByRoleV2(@Param("role") String role,
                           @Param("companyId") String companyId);

    List<Map> showAllAccounts();
    List<Map> showAllAccountsV2(String companyId);

    List<Map> showOrganizationTree();
    List<Map> showOrganizationTreeV2(String companyId);

    void UpdateAccount(Map map);

    void DeleteRoleByAccountId(Map map);

    void InserNewRoleByAccountId(Map map);

    void UpdateAccountOrgById(Map map);

    void AddNewAccount(Map map);

    void UpdateAccountStateById(Map map);

    void updateAccountSignature(Map map);

    void saveNewOrg(Map map);

    void updateOrg(Map map);

    void SwitchSourceMenubyId(Map map01);

    String GetSourceMenuByUsername(String username);

    List<Map> findByUsername(String username);

    List<Map> getAccountAccessAreas(String username);

    List<Map> getSapList();
    List<Map> getKeeperList();

    List<Map> getTEList();
    List<Map> getRentalList();

    void updateRentalHistoryDate(Map map);

    void updateSapIssueDate(Map map);
    void updateKeeperIssueDate(Map map);

    void updateTePurchaseDate(Map map);

    List<OrgUserRelationResponse> getOrgUserRelation(@Param("accountId") String accountId);

    List<Map> showOrgTreeWithParent();

    void updateNewUserPassword(Map<String, Object> requestMap);

    void DeleteRoleOrgByAccountId(Map map);

    void InsertNewRoleOrgByAccount(Map map);

    void updateAccountMainRoleOrgSetup(Map mapMainParams);

    List<Map> GetRoleOrgListByUserid(String userid);

    void UpdateAccountByUserId(Map map);

    void SwitchAccountRoleOrg(Map map);

    Map GetPathByOrgId(String orgid);

    void saveNewOrgV2(Map mapParams);

    void updatePathfromNewSaveOrg(Map mapReq);

    void updateNewOrganizationById(Map mapParams);

    Map getAccountById(Map req);

    List<Map> getKeeperMaterialList(Map req);
}