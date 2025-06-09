package com.microservice.systemservice.repository;

import com.code.share.codesharing.excel.model.UserAccountExcel;
import com.microservice.systemservice.models.UserAccount;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface AccountRepository {
    List<Map> showAllAccounts();

    List<Map> showAllRoles();

    List<Map> showOrganizationTree();

    void DeleteRoleOrgByAccountId(Map map);

    void updateAccountMainRoleOrgSetup(Map mapMainParams);

    void UpdateAccountByUserId(Map map);

    void UpdateAccountStateById(Map map);

    void SwitchSourceMenubyId(Map map01);

    void updateNewUserPassword(Map<String, Object> requestMap);

    List<Map> GetRoleOrgListByUserid(String userid);

    void InsertNewUserAccount(UserAccountExcel userAccount);

    Map getOrganizatioById(String organizationId);

    void InsertNewRoleOrgByAccount(Map mapInsRoleOrg);

    void AddNewAccount(Map map);

    void updateAccountSignature(Map map);

    String getAllAccountNames();

    List<Map> showAllAccountsByOrg(Map userInfo);

    Map getWidgetPermissionAcc(Map mapReq);
}
