package com.microservice.systemservice.controllers;

import com.code.share.codesharing.tools.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.systemservice.models.GetResponse;
import com.microservice.systemservice.models.OrgClass;
import com.microservice.systemservice.repository.RoleRepository;
import com.microservice.systemservice.repository.SystemRepository;
import com.microservice.systemservice.utils.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import com.microservice.systemservice.services.ActivityLogService;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/system/role")
@RequiredArgsConstructor
@Slf4j
public class RoleController {

    @Autowired
    private ActivityLogService activityLogService;
    @Autowired
    private SystemRepository systemRepository;

    @Autowired
    private RoleRepository roleRepository;

    @PostMapping("/saveAssignUser")
    public void saveAssignUser(@RequestBody GetResponse response) {

        Object obj = response.getObject();
        ObjectMapper oMapper = new ObjectMapper();
        Map map = oMapper.convertValue(obj, Map.class);

        String UserId = response.getStr();
        String companyId = response.getCompanyId();

        String roleId = roleRepository.getUserRole(UserId);

        List<String> resourcesIds = new ArrayList<>();
        for ( Object key : map.keySet() ) {
            Object map1 = map.get(key);
            Map map2 = oMapper.convertValue(map1, Map.class);

            boolean isCheck = (boolean)map2.get("checked");
            if(!isCheck) {
                isCheck = (boolean)map2.get("partialChecked");
            }

            if(isCheck) resourcesIds.add(key.toString());
        }

        //roleRepository.DeleteResourceByUserId(roleId, UserId); //V1
        roleRepository.DeleteResourceByUserIdV2(roleId, UserId, companyId);

        for(String resourceId : resourcesIds){
            //
            Map map4 = new HashMap();
            map4.put("resourceId", resourceId);
            map4.put("roleId", roleId);
            map4.put("userId", UserId);
            map4.put("companyId", companyId);

            roleRepository.InsertNewResourceByUser(map4);

            Map map5 = map4;
        }
    }

    @PostMapping("/saveNewResources")
    public Map saveNewResources(@RequestBody GetResponse response){

        boolean statusGood = true;
        Object obj = response.getObject();
        Map map = null;
        if(ObjectUtils.isEmpty(obj)){
            map = new HashMap();
        } else {
            map = new ObjectMapper().convertValue(obj, Map.class);
        }

        if(!map.containsKey("NAME_") || !map.containsKey("LINK") || !map.containsKey("ICON_URL")){
            statusGood = false;
        }

        //List<Map> resResources = Util.me.GetData("showAllResources", systemRepository.showAllResources());
        List<Map> resResources = roleRepository.showAllResources();

        if(!map.containsKey("PARENTID")){
            map.put("PARENTID", null);
            map.put("TYPE_", "1");

            OptionalInt maxSort = resResources.stream()
                    .filter(x -> x.get("SORT") != null)
                    .map(x -> {
                        String sortValue = (String) x.get("SORT");
                        try {
                            return Integer.parseInt(sortValue);
                        } catch (NumberFormatException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .mapToInt(Integer::intValue)
                    .max();

            map.put("SORT", maxSort.isPresent() ? maxSort.getAsInt() + 1 : 1);

            //
            List<Map> selParentResc = resResources.stream().filter(mapp -> mapp.get("TYPE_").toString().equals("1"))
                    .collect(Collectors.toList());
            //

            int size = selParentResc.size();

            if (size > 0) {
                String code = selParentResc.get(size - 1).get("CODE_").toString();
                int codeInt = Integer.parseInt(code);
                codeInt +=1;

                code = String.valueOf(codeInt).length() == 1 ? "0"+codeInt: ""+codeInt ;

                map.put("CODE_", code);
            } else {
                map.put("CODE_", "01");
            }
        } else {
            map.put("TYPE_", "2");

            String id = map.get("PARENTID").toString();
            //
            List<Map> selParentResc = resResources.stream().filter(mapp -> mapp.get("RESOURCES_ID").toString().equals(id))
                    .collect(Collectors.toList());
            //
            Map map1 = selParentResc.get(0);

            List<Map> sel2Resc = resResources.stream().filter(mapp ->
                            mapp.get("CODE_").toString().startsWith(map1.get("CODE_").toString())
                                    && mapp.get("TYPE_").equals("2")
                    )
                    .collect(Collectors.toList());

            int size = sel2Resc.size();

            String newCode = null;
            if (size > 0) {
                String code = sel2Resc.get(size - 1).get("CODE_").toString();
                code = code.substring(code.length() - 2);
                int codeInt = Integer.parseInt(code);
                codeInt +=1;
                code = String.valueOf(codeInt).length() == 1 ? "0"+codeInt: ""+codeInt ;

                newCode = selParentResc.get(0).get("CODE_") + code;
            } else {
                newCode = selParentResc.get(0).get("CODE_") + "01";
            }

            map.put("CODE_", newCode);
        }

        if (statusGood) {
            roleRepository.saveNewResources(map);
            Util.me.DeleteFile("showAllResources");
        }

        Map feedback = new HashMap();
        if(statusGood){
            feedback.put("feedback", "1");
        } else {
            feedback.put("feedback", "0");
        }
        return feedback;
    }

    @PostMapping("/removeUser")
    public void removeUser(@RequestBody GetResponse response){

        String accountid = response.getStr();
        Map map = new HashMap();

        map.put("ACCOUNT_ID", accountid);

        roleRepository.DeleteAcctInRole(map);

        //Util.me.DeleteFile("showAllAccounts");
    }

    @PostMapping("/actionRole")
    public Map actionRole(@RequestBody GetResponse response){

        boolean statusGood = true;
        Object obj = response.getObject();
        Map map = null;
        if(ObjectUtils.isEmpty(obj)){
            map = new HashMap();
        } else {
            map = new ObjectMapper().convertValue(obj, Map.class);
        }

        if(!map.containsKey("NAME_") ){
            statusGood = false;
        }

        String actionType = response.getAction();

        String createdById = null;
        String createdByName = null;

        if (map.containsKey("CREATED_BY_ID") && map.containsKey("CREATED_BY_NAME")) {
            createdById = map.get("CREATED_BY_ID").toString();
            createdByName = map.get("CREATED_BY_NAME").toString();
        }

        if (statusGood && createdById != null && createdByName != null) {
            if (actionType.equals("add")) {
                roleRepository.saveNewRole(map);
                activityLogService.logActivity("Manage Roles", "Added Role", createdById, createdByName);
            } else {
                roleRepository.updateRoleById(map);
                activityLogService.logActivity("Manage Roles", "Updated Role", createdById, createdByName);
            }
            //Util.me.DeleteFile("showAllRoles");
        }

        Map feedback = new HashMap();
        if(statusGood){
            feedback.put("feedback", "1");
        } else {
            feedback.put("feedback", "0");
        }
        return feedback;
    }


    @GetMapping("showAllRoles")
    public Map showAllRoles(String companyId){
        List<Map> resRoles =  roleRepository.showAllRoles();
        //List<Map> resRoles = Util.me.GetData("showAllRoles", systemRepository.showAllRoles());
        List<Map> resResources = roleRepository.showAllResources();
        //List<Map> resResources = Util.me.GetData("showAllResources", systemRepository.showAllResources());

        //List<Map> resRoleResourse = roleRepository.getResourceRoleAccount(); //V1
        List<Map> resRoleResourse = roleRepository.getResourceRoleAccountV2(companyId);
        //List<Map> resRoleResourse = Util.me.GetData("getResourceRoleAccount", systemRepository.getResourceRoleAccount());
        //List<Map> resAccounts = Util.me.GetData("showAllAccounts", systemRepository.showAllAccounts());

        //List<Map> resAccounts = roleRepository.showAllAccounts(); //V1
        List<Map> resAccounts = roleRepository.showAllAccountsV2(companyId);
        //
        List<Map> mapLevelOne = resResources.stream().filter(map -> map.get("TYPE_").toString().equals("1"))
                .collect(Collectors.toList());
        //
        List<OrgClass> resourceTree = new ArrayList<>();
        for(Map map001 : mapLevelOne){ // level 1
            //
            List<Map> mapLevelTwo = resResources.stream().filter(map -> map.get("CODE_").toString().startsWith(map001.get("CODE_").toString()))
                    .collect(Collectors.toList());
            if (mapLevelTwo.size() != 0 ) mapLevelTwo.remove(0);
            List<OrgClass> resourceTree002 = new ArrayList<>();
            for(Map map002: mapLevelTwo){ // level 2
                resourceTree002.add(new OrgClass(map002.get("RESOURCES_ID").toString(), map002.get("NAME_").toString()));
            }
            //
            resourceTree.add(new OrgClass(map001.get("RESOURCES_ID").toString(), map001.get("NAME_").toString(), resourceTree002));
        }
        // widget permission
        List<Map> mapWidgUser = new ArrayList<>();
        //mapWidgUser = roleRepository.getUserWidgetPermissionData(); //V1
        mapWidgUser = roleRepository.getUserWidgetPermissionDataV2(companyId);
        List<Map> mapNewWidgUser = new ArrayList<>();

        for(Map mapFor: mapWidgUser){
            String json = mapFor.get("json_permission").toString();
            Map mapConvWidgUser = null;

            mapConvWidgUser = Utils.xo().conv().JsonStrToMap(json);
            mapNewWidgUser.add(mapConvWidgUser);
        }
        //
        List<Map> mapWidgRole = new ArrayList<>();
        //mapWidgRole = roleRepository.getRoleWidgetPermissionData(); //V1
        mapWidgRole = roleRepository.getRoleWidgetPermissionDataV2(companyId);
        List<Map> mapNewWidgRole = new ArrayList<>();

        for(Map mapFor: mapWidgRole){
            String json = mapFor.get("json_permission").toString();
            Map mapConvWidgUser = null;

            mapConvWidgUser = Utils.xo().conv().JsonStrToMap(json);
            mapNewWidgRole.add(mapConvWidgUser);
        }
        //
        List<Map> widgeLegend = roleRepository.getWidgeLegend();
        List<JSONObject> listLegend = new ArrayList<>();

        for(Map wl : widgeLegend){
            String str = wl.get("widge_legend").toString();

            JSONObject json = Utils.xo().conv().StrToJson(str);

            listLegend.add(json);
        }

        Map mapRet = new HashMap();

        mapRet.put("queryRoles", resRoles);
        mapRet.put("treeResources", resourceTree);
        mapRet.put("queryRoleResources", resRoleResourse);
        mapRet.put("queryAccounts", resAccounts);
        mapRet.put("queryResources", resResources);

        mapRet.put("queryWidgeUser", mapNewWidgUser);
        mapRet.put("queryWidgeRole", mapNewWidgRole);
        mapRet.put("queryWidgeLegend", listLegend);


        return mapRet;
    }

    @PostMapping("/saveAssignRoles")
    public void saveAssignRoles(@RequestBody GetResponse response){

        Object obj = response.getObject();
        ObjectMapper oMapper = new ObjectMapper();
        Map map = oMapper.convertValue(obj, Map.class);

        String roleId = response.getStr();
        String companyId = response.getCompanyId();

        List<String> resourcesIds = new ArrayList<>();
        for ( Object key : map.keySet() ) {
            Object map1 = map.get(key);
            Map map2 = oMapper.convertValue(map1, Map.class);

            boolean isCheck = (boolean)map2.get("checked");
            if(!isCheck) {
                isCheck = (boolean)map2.get("partialChecked");
            }

            if(isCheck) resourcesIds.add(key.toString());
        }

        //String wee = "wee";
        roleRepository.DeleteResourceByRoleId(roleId, companyId);

        for(String resourceId : resourcesIds){
            //
            Map map4 = new HashMap();
            map4.put("resourceId", resourceId);
            map4.put("roleId", roleId);
            map4.put("companyId", companyId);

            roleRepository.InsertNewResourceByRole(map4);
        }
        //
        //Util.me.DeleteFile("getResourceRoleAccount");
    }

    @PostMapping("/roleActivation")
    public void roleActivation(@RequestBody GetResponse response){

        Object obj = response.getObject();
        ObjectMapper oMapper = new ObjectMapper();
        Map map = oMapper.convertValue(obj, Map.class);

        String state = map.get("STATE").toString();
        state = state.equals("1") ? "0": "1" ;

        map.put("STATE",state);
        roleRepository.UpdateRoleStateById(map);
        //Util.me.DeleteFile("showAllRoles");

        String createdById = response.getUserId();
        String createdByName = response.getUserFullName();
        int st = Integer.parseInt(state);
        String actions = "";
        if (st == 1){
            actions = "Enable Role";
        } else {
            actions = "Disable Role";
        }
        activityLogService.logActivity("Role Management", actions, createdById, createdByName);
    }

    @PostMapping("/saveSelUsers")
    public void saveSelUsers(@RequestBody GetResponse response){

        Object obj = response.getObject();
        ObjectMapper oMapper = new ObjectMapper();
        List<Map> map = oMapper.convertValue(obj, List.class);

        String roleId = response.getStr();

        List<Map> mapList = new ArrayList<>();

        for(Map map2 : map){
            if(map2.containsKey("isNew")){
                map2.put("roleID", roleId);
                mapList.add(map2);
            }
        }

        for(Map map3: mapList){
            roleRepository.DeleteAcctInRole(map3);
        }

        for(Map map4: mapList){
            roleRepository.SaveNewAcctInRole(map4);
        }

        //Util.me.DeleteFile("showAllAccounts");
    }

    @PostMapping("/updateResourceByRow")
    public void updateRentalsByRow(@RequestBody GetResponse response){
//
        Object obj = response.getObject();
        Map mapRes = new ObjectMapper().convertValue(obj, Map.class);

        String one = "123";

        roleRepository.updateResourceByRow(mapRes);
    }

    @PostMapping("/btnPermissionManager")
    public void BtnPermissionManager(@RequestBody Map<String, Object> request){
        //
        Map buttonPermissionLegend = roleRepository.getButtonPermissionLegend();
        String widgeList = buttonPermissionLegend.get("code").toString();
        String widgeListLabel = buttonPermissionLegend.get("label").toString();

        String[] widgeList_ = widgeList.split(",");
        String[] widgeListLabel_ = widgeListLabel.split(",");

        String actionType = request.get("type").toString();

        String userid = MapUtils.getString(request, "userid", "0");
        String roleid = MapUtils.getString(request, "roleid", "0");
        String companyId = MapUtils.getString(request, "companyId", "0");

        String resourceid = request.get("resourceid").toString();


        List<String> userWidgeList = (List<String>) request.get("widgelist");
        //List userWidgeList_ = Arrays.asList(userWidgeList);

        Map widgeMap = new HashMap();
        widgeMap.put("userid", userid);
        widgeMap.put("roleid", roleid);
        widgeMap.put("resourceid", resourceid);
        widgeMap.put("display", "-");
        widgeMap.put("type", "button");
        widgeMap.put("widge_code", "-");
        widgeMap.put("label", "-");
        widgeMap.put("action_type", actionType);
        widgeMap.put("companyId", companyId);

        // delete first all data by userid and resourceid
        roleRepository.truncateFirstBeforeInsert(widgeMap);
        //

        for(int i=0; i < widgeList_.length; i++){
            //
            boolean isMatch = userWidgeList.contains(widgeList_[i]);
            if(isMatch){
                widgeMap.put("display", "flex");
                widgeMap.put("widge_code",widgeList_[i]);
                widgeMap.put("label", widgeListLabel_[i]);
                roleRepository.AddNewWidgePermByUser(widgeMap);
            } else {
                widgeMap.put("display", "hidden");
                widgeMap.put("widge_code",widgeList_[i]);
                widgeMap.put("label", widgeListLabel_[i]);
                roleRepository.AddNewWidgePermByUser(widgeMap);
            }
            //
        }
    }
}