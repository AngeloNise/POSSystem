package com.fiberhome.authservice.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ctc.wstx.util.StringUtil;
import com.fiberhome.authservice.dto.response.OrganizationResponseDTO;
import com.fiberhome.authservice.model.ActivityLog;
import com.fiberhome.authservice.model.LoginUserDetails;
import com.fiberhome.authservice.model.OrgUserRelation;
import com.fiberhome.authservice.repository.LoginUserDetailsMapper;
import com.fiberhome.authservice.security.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Angel
 * @created 13/01/2023 - 9:44 AM
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final JwtTokenUtils jwtTokenUtils;
    private final SystemService systemService;

    private final LoginUserDetailsMapper loginUserDetailsMapper;


    public Map<String,Object> handleAuthenticationObjects(Authentication authentication, LoginUserDetails authUser,
                                                          List<String> roles) {
        List<OrganizationResponseDTO> orgList = new ArrayList<>();
        try {
            Map<String,Object> jwt = jwtTokenUtils.generateJwtToken(authentication);
            List<OrganizationResponseDTO> organizationTree = systemService.organizationListTree();
            //List<OrganizationResponseDTO> LedgerOrganizationTree = systemService.ledgerOrganizationListTree();

            if(!CollectionUtils.contains(Collections.enumeration(roles),"ROLE_SUPER ADMINISTRATOR")){
                List<OrgUserRelation> orgUserRelationList = systemService.getUserOrgRelationByAccountId(
                        String.valueOf(authUser.getId()));
                List<String> areaIdList = new ArrayList<>();


                try {
                    if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(orgUserRelationList)){
                        areaIdList = orgUserRelationList.stream().map(OrgUserRelation::getAreaId).collect(Collectors.toList());
                            orgList = filterByOrganizationId(organizationTree,areaIdList);
                    }else{
                        //Get Area Level of Org User
                            orgList = filterByOrganizationId(organizationTree,List.of(authUser.getOrganizationId()));
                    }
                } catch (Exception e) { }


                if(org.apache.commons.collections4.CollectionUtils.isEmpty(orgList)){
                    //Get Project Level of Org User
                    orgList = organizationTree.stream().filter(o->authUser.getOrganizationId()
                                    .equals(String.valueOf(o.getKey())))
                            .collect(Collectors.toList());
                }else{
                    //Trim unmatched child organization(areas)
                    List<String> finalAreaIdList = areaIdList;
                    orgList.forEach(org -> org.getChildren()
                            .removeIf(child -> !finalAreaIdList.contains(child.getKey()))
                    );
                }
            }
            //
            List<OrganizationResponseDTO> LedgerOrganizationTree = systemService.ledgerOrganizationListTree();
            List<OrganizationResponseDTO> LedgerOrganizationAreaTree = systemService.ledgerOrganizationListAreaTree();
            List<Map> OrganizationGrade1 = systemService.organizationGrade1();
            List<Map> OrganizationGrade2 = systemService.organizationGrade2();
            List<Map> OrganizationGrade3 = systemService.organizationGrade3();
            List<Map> OrganizationGrade4 = systemService.organizationGrade4();
            //String userInfos = systemService.getUserInformation();

            Map userMap = loginUserDetailsMapper.getUserByID(authUser.getId());

            // =============================>
            organizationPathNameDisplay(userMap);
            // =============================<

            //for activity log
            ActivityLog activityLog = new ActivityLog();
            activityLog.setPageName("Authorization Service");
            activityLog.setAction("Login");
            activityLog.setCreatedDate(new Date());
            activityLog.setCreatedById(userMap.get("ACCOUNT_ID").toString());
            activityLog.setCreatedByName(userMap.get("FULL_NAME").toString());

            loginUserDetailsMapper.insertActivityLog2(activityLog);

            return Map.of(
                    "jwt",jwt,
                    "org",CollectionUtils.isEmpty(orgList)? organizationTree: orgList,
                    "orgLedger", LedgerOrganizationTree,
                    "orgLedgerArea", LedgerOrganizationAreaTree,
                    "userDetails", userMap,
                    "organizationGrade1", OrganizationGrade1,
                    "organizationGrade2", OrganizationGrade2,
                    "organizationGrade3", OrganizationGrade3,
                    "organizationGrade4", OrganizationGrade4
            );


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void organizationPathNameDisplay(Map userMap) {
        //
        List<Map> organizationList = loginUserDetailsMapper.GetAllOrganizationList();

        String rolorgIds = userMap.get("rolorgids").toString();

        String[] orgSplit = rolorgIds.split(",");

        List<String> finalConcatOrg = new ArrayList<>();

        for(String roleOrgId: orgSplit){
            String orgId = roleOrgId.split(":")[1];

            List<Map> orgSel = organizationList.stream().filter(xo -> xo.get("id_").toString().equals(orgId)).collect(Collectors.toList());

            String path = orgSel.get(0).get("path_").toString();

            String[] pathArr = path.split("\\.");

            List<String> organiConcat = new ArrayList<>();

            for(String pathId: pathArr){
                List<Map> org = organizationList.stream().filter(xo -> xo.get("id_").toString().equals(pathId)).collect(Collectors.toList());
                String name_ = org.get(0).get("name_").toString();
                organiConcat.add(name_);
            }

            String wholeOrg = StringUtils.join(organiConcat, '.');
            finalConcatOrg.add(wholeOrg);

        }
        String[] finalConcatArr = new String[finalConcatOrg.size()];
        finalConcatOrg.toArray(finalConcatArr);

        String roleOrgName = userMap.get("rolorgnames").toString();
        String[] roleOrgArr = roleOrgName.split(",");


        List<String> finalfinalConcatOrg = new ArrayList<>();

        for(int ii = 0; ii < roleOrgArr.length; ii++){
            //
            String res = roleOrgArr[ii].split(":")[0] +":"+ finalConcatArr[ii];

            finalfinalConcatOrg.add(res);
            //
        }

        String finalAdvent = StringUtils.join(finalfinalConcatOrg, ",");

        userMap.put("rolorgnames",finalAdvent);
    }

    private List<OrganizationResponseDTO> filterByOrganizationId(List<OrganizationResponseDTO> orgList, List<String> organizationId){
        /*return orgList.stream()
                .filter(parent->parent.getChildren().stream()
                        .anyMatch(children -> children != null && organizationId.contains(String.valueOf(children.getKey()))))
                //.anyMatch(children-> children!=null && organizationId.equals(String.valueOf(children.getKey()))))
                .collect(Collectors.toList());*/
        return orgList.stream()
                .filter(parent -> {
                    List<OrganizationResponseDTO> children = parent.getChildren();
                    return children != null && children.stream()
                            .anyMatch(child -> child != null && organizationId.contains(String.valueOf(child.getKey())));
                })
                .collect(Collectors.toList());

    }
}
