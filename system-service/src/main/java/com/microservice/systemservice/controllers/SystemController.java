package com.microservice.systemservice.controllers;

import com.code.share.codesharing.model.Employee;
import com.code.share.codesharing.tools.Systemm;
import com.code.share.codesharing.tools.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;
import com.microservice.systemservice.dto.OrgUserRelationResponse;
import com.microservice.systemservice.helper.SerialDate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.systemservice.models.GetResponse;
import com.microservice.systemservice.models.MenuClass;
import com.microservice.systemservice.models.OrgClass;
import com.microservice.systemservice.repository.SystemRepository;
import com.microservice.systemservice.services.ProjectsService;
import com.microservice.systemservice.utils.OrganizationBean;
import com.microservice.systemservice.utils.Sync;
import com.microservice.systemservice.utils.Util;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("rawtypes")
@RestController
@RequestMapping("/system/menu")
@RequiredArgsConstructor
@Slf4j
public class SystemController {

    @Value("${Account.signaturePath}")
    String signatureUploadPath;

    private final SystemRepository systemRepository;
    //private final ApplicationContext context;
    //private final ProjectsService projectsService;

    @GetMapping("/show")
    public String showString(){

        Employee employee = new Employee();

        Utils.xo().syst().testPost();

        //return "SYSTEM-SERVICE IS IN THE HOUSE";
        return employee.Output();
    }

    @GetMapping("/deleteJSON")
    public void deleteJSON(){

        Util.me.DeleteFile("showAllRoles");
        Util.me.DeleteFile("showOrganizationTree");
        Util.me.DeleteFile("showAllAccounts");
        Util.me.DeleteFile("getResourceRoleAccount");
        Util.me.DeleteFile("showAllResources");

        Util.me.DeleteFile("showAllEquipments");

    }

    @PostMapping("/updateOrg")
    @Transactional(propagation = Propagation.REQUIRES_NEW,isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public Map<String,Object> updateOrg(@RequestBody Map<String, Object> request){

        Map mapReq = (Map) request.get("object");
        int orgType = Integer.parseInt(mapReq.get("orgType").toString());

        Map mapParams = new HashMap();

        switch (orgType){
            case 1:
                mapParams.put("name", mapReq.get("name").toString());
                mapParams.put("parent_id", "0");
                mapParams.put("type", mapReq.get("orgType").toString());
                mapParams.put("path", mapReq.get("orgOldId").toString());
                mapParams.put("orgId", mapReq.get("orgOldId").toString());
                mapParams.put("costCenter", MapUtils.getString(mapReq, "constCenter",""));
                mapParams.put("wbs", MapUtils.getString(mapReq, "wbs",""));

                break;
            case 2:
                mapParams.put("name", mapReq.get("name").toString());
                mapParams.put("parent_id", mapReq.get("department").toString());
                mapParams.put("type", mapReq.get("orgType").toString());
                mapParams.put("costCenter", MapUtils.getString(mapReq, "constCenter",""));
                mapParams.put("wbs", MapUtils.getString(mapReq, "wbs",""));

                String path2_ = mapReq.get("department").toString()
                        +"."+
                        mapReq.get("orgOldId").toString();
                mapParams.put("path", path2_);
                mapParams.put("orgId", mapReq.get("orgOldId").toString());

                break;
            case 3:
                mapParams.put("name", mapReq.get("name").toString());
                mapParams.put("parent_id", mapReq.get("region").toString());
                mapParams.put("type", mapReq.get("orgType").toString());
                //mapParams.put("costCenter", mapReq.get("costCenter").toString());
                mapParams.put("costCenter", MapUtils.getString(mapReq, "constCenter",""));
                mapParams.put("wbs",MapUtils.getString(mapReq, "wbs",""));

                String path3_ = mapReq.get("department").toString()
                        +"."+
                        mapReq.get("region").toString()
                        +"."+
                        mapReq.get("orgOldId").toString();
                mapParams.put("path", path3_);
                mapParams.put("orgId", mapReq.get("orgOldId").toString());

                break;
            case 4:
                mapParams.put("name", mapReq.get("name").toString());
                mapParams.put("parent_id", mapReq.get("area").toString());
                mapParams.put("type", mapReq.get("orgType").toString());
                mapParams.put("costCenter", MapUtils.getString(mapReq, "constCenter",""));
                mapParams.put("wbs", MapUtils.getString(mapReq, "wbs",""));

                String path4_ = mapReq.get("department").toString()
                        +"."+
                        mapReq.get("region").toString()
                        +"."+
                        mapReq.get("area").toString()
                        +"."+
                        mapReq.get("orgOldId").toString();

                mapParams.put("path", path4_);
                mapParams.put("orgId", mapReq.get("orgOldId").toString());

                break;

        }

        Map feedback = Utils.xo().sync().GetFeedback(
                () -> {
                    systemRepository.updateNewOrganizationById(mapParams);
                }
        );

        //#region old codes
        /*boolean statusGood = true;
        Object obj = response.getObject();
        Map<String,Object> map = new HashMap<>();
        if(!ObjectUtils.isEmpty(obj)){
            map = new ObjectMapper().convertValue(obj, new TypeReference<>(){});
        }
        if(!map.containsKey("FULL_NAME") || !map.containsKey("PARENT_ID")){
            statusGood = false;
        }
        if(!MapUtils.getString(map,"PARENT_ID").equalsIgnoreCase("1")
                && !MapUtils.getString(map,"PARENT_ID").equalsIgnoreCase("0")){
            MapUtils.safeAddToMap(map,"GRADE_ID",2);
        }else{
            MapUtils.safeAddToMap(map,"GRADE_ID",1);
        }
        if (statusGood) {
            systemRepository.updateOrg(map);
            //Util.me.DeleteFile("showOrganizationTree");
            //context.getBean(OrganizationBean.class);
            OrganizationBean.init();

        }

        Map<String,Object> feedback = new HashMap<>();
        if(statusGood){
            feedback.put("feedback", "1");
            MapUtils.safeAddToMap(feedback,"key",MapUtils.getIntValue(map,"ORGANIZATION_ID"));
            MapUtils.safeAddToMap(feedback,"parentId",MapUtils.getIntValue(map,"PARENT_ID"));
            MapUtils.safeAddToMap(feedback,"label",MapUtils.getString(map,"FULL_NAME"));
        } else {
            feedback.put("feedback", "0");
        }*/
        //#endregion

        return feedback;
    }

    @PostMapping("/saveNewOrg")
    @Transactional(propagation = Propagation.REQUIRES_NEW,isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public Map<String,Object> saveNewOrg(@RequestBody Map<String, Object> request){

        /*ObjectMapper oMapper = new ObjectMapper();
        Map mapReq = oMapper.convertValue(request, Map.class);*/

        Map mapReq = (Map) request.get("object");

        int orgType = Integer.parseInt(mapReq.get("orgType").toString());

        Map mapParams = new HashMap();

        switch (orgType){
/*            case 1:
                mapParams.put("name", mapReq.get("name").toString());
                mapParams.put("parent_id", "0");
                mapParams.put("type", mapReq.get("orgType").toString());
                mapParams.put("costCenter", mapReq.get("costCenter").toString());
                mapParams.put("wbs", mapReq.get("wbs").toString());

                systemRepository.saveNewOrgV2(mapParams);

                String path1_ = mapParams.get("NEW_ID").toString();
                mapParams.put("path", path1_);
                systemRepository.updatePathfromNewSaveOrg(mapParams);

                break;*/
            case 2:
                mapParams.put("name", mapReq.get("name").toString());
                mapParams.put("parent_id", mapReq.get("department").toString());
                mapParams.put("type", mapReq.get("orgType").toString());
                mapParams.put("costCenter", mapReq.get("costCenter").toString());
                mapParams.put("wbs", mapReq.get("wbs").toString());

                systemRepository.saveNewOrgV2(mapParams);

                String path2_ = mapReq.get("department").toString()
                        +"."+
                        mapParams.get("NEW_ID").toString();
                mapParams.put("path", path2_);
                systemRepository.updatePathfromNewSaveOrg(mapParams);

                break;
            case 3:
                mapParams.put("name", mapReq.get("name").toString());
                mapParams.put("parent_id", mapReq.get("region").toString());
                mapParams.put("type", mapReq.get("orgType").toString());
                mapParams.put("costCenter", mapReq.get("costCenter").toString());
                mapParams.put("wbs", mapReq.get("wbs").toString());

                systemRepository.saveNewOrgV2(mapParams);

                String path3_ = mapReq.get("department").toString()
                        +"."+
                        mapReq.get("region").toString()
                        +"."+
                        mapParams.get("NEW_ID").toString();
                mapParams.put("path", path3_);
                systemRepository.updatePathfromNewSaveOrg(mapParams);
                break;
            case 4:

                mapParams.put("name", mapReq.get("name").toString());
                mapParams.put("parent_id", mapReq.get("area").toString());
                mapParams.put("type", mapReq.get("orgType").toString());
                mapParams.put("costCenter", mapReq.get("costCenter").toString());
                mapParams.put("wbs", mapReq.get("wbs").toString());

                systemRepository.saveNewOrgV2(mapParams);

                String path4_ = mapReq.get("department").toString()
                        +"."+
                        mapReq.get("region").toString()
                        +"."+
                        mapReq.get("area").toString()
                        +"."+
                        mapParams.get("NEW_ID").toString();
                mapParams.put("path", path4_);
                systemRepository.updatePathfromNewSaveOrg(mapParams);
                break;
        }

        //#region old codes backup
        /*boolean statusGood = true;

        Object obj = response.getObject();
        Map<String,Object> map = new HashMap<>();
        if(!ObjectUtils.isEmpty(obj)){
            map = new ObjectMapper().convertValue(obj, new TypeReference<>(){});
            map.put("organizationId",null);
        }
        if(!map.containsKey("FULL_NAME") || !map.containsKey("PARENT_ID")){
            statusGood = false;
        }
        if(!MapUtils.getString(map,"PARENT_ID").equalsIgnoreCase("1")
                && !MapUtils.getString(map,"PARENT_ID").equalsIgnoreCase("0")){
            MapUtils.safeAddToMap(map,"GRADE_ID",2);
        }else{
            MapUtils.safeAddToMap(map,"GRADE_ID",1);
        }

        if (statusGood) {
            systemRepository.saveNewOrg(map);
            //Util.me.DeleteFile("showOrganizationTree");
           // context.getBean(OrganizationBean.class).init();
            OrganizationBean.init();
        }

        Map<String,Object> feedback = new HashMap<>();

        if(statusGood){
            feedback.put("feedback", "1");
            MapUtils.safeAddToMap(feedback,"key",MapUtils.getIntValue(map,"organizationId"));
            MapUtils.safeAddToMap(feedback,"parentId",MapUtils.getIntValue(map,"PARENT_ID"));
            MapUtils.safeAddToMap(feedback,"label",MapUtils.getString(map,"FULL_NAME"));
            feedback.put("children",null);
        } else {
            feedback.put("feedback", "0");
        }*/
        //#endregion

        return null;
    }

    @GetMapping("/getResourceRoleAccount")
    public void getResourceRoleAccount(){
        //return systemRepository.getResourceRoleAccount();

        List<Map> resAccounts = systemRepository.showAllAccounts();

        resAccounts.forEach(stringObjectMap -> {
            stringObjectMap.remove("CREATE_DATE");
        });

        Object[] object = new Object[1];
        object[0] = resAccounts;

        Util.me.SaveUniMapper("","","showAllAccounts", object);

    }

    @GetMapping("showOrganizationTree")
    public Map showOrganizationTree(@RequestParam String companyId){

        //List<Map> res = systemRepository.showOrganizationTree(); //V1
        List<Map> res = systemRepository.showOrganizationTreeV2(companyId);

        //NOTE: [Commented 2024-06-06] Queries org from unused table
        //List<Map> orgWitParent = systemRepository.showOrgTreeWithParent();

        //List<Map> resAccounts = systemRepository.showAllAccounts(); //V1
        List<Map> resAccounts = systemRepository.showAllAccountsV2(companyId);

        String[] parentIds = res.stream().map(x-> x.get("PARENT_ID").toString()).toArray(String[]::new);

        LinkedHashSet<String> lhSetColors = new LinkedHashSet<String>(Arrays.asList(parentIds));
        String[] parentOrgId = lhSetColors.toArray(new String[ lhSetColors.size() ]);

        List<OrgClass> orgClassListLevel1 = new ArrayList<>();
        orgClassListLevel1 = CreateOrganizationTree(res, parentOrgId);

        Map mapRes = new HashMap();

        mapRes.put("orgTree", orgClassListLevel1 );
        mapRes.put("queryOrig", res);
        //mapRes.put("queryOrigV2", orgWitParent);
        mapRes.put("queryAcc", resAccounts);

        return mapRes;
    }

    private List<OrgClass> CreateOrganizationTree(List<Map> res, String[] parentOrgId) {

        List<OrgClass> orgClassListLevel1 = new ArrayList<>();

        for(String parentID: parentOrgId){ // LEVEL 1
            List<Map> mapFillFilter = res.stream().filter(map -> map.get("PARENT_ID").toString().equals(parentID))
                    .collect(Collectors.toList());

            for(Map org : mapFillFilter){ // LEVEL 2
                //
                List<Map> mapFilllevel2 = res.stream().filter(map -> map.get("PARENT_ID").toString().equals(org.get("ORGANIZATION_ID").toString()))
                        .collect(Collectors.toList());
                mapFilllevel2.sort(Comparator.comparing(m -> (String)m.get("FULL_NAME")));

                List<OrgClass> orgClassListLevel2 = new ArrayList<>();

                for(Map org3: mapFilllevel2){ // LEVEL 3
                    //
                    List<Map> mapFilllevel3 = res.stream().filter(map -> map.get("PARENT_ID").toString().equals(org3.get("ORGANIZATION_ID").toString()))
                            .collect(Collectors.toList());
                    mapFilllevel3.sort(Comparator.comparing(m -> (String)m.get("FULL_NAME")));

                    List<OrgClass> orgClassListLevel3 = new ArrayList<>();

                    for(Map org4: mapFilllevel3){ // LEVEL 4
                        //
                        List<Map> mapFilllevel4 = res.stream().filter(map -> map.get("PARENT_ID").toString().equals(org4.get("ORGANIZATION_ID").toString()))
                                .collect(Collectors.toList());
                        mapFilllevel4.sort(Comparator.comparing(m -> (String)m.get("FULL_NAME")));

                        List<OrgClass> orgClassListLevel4 = new ArrayList<>();

                        for(Map org5: mapFilllevel4) { // LEVEL 5
                            orgClassListLevel4.add(new OrgClass(org5.get("ORGANIZATION_ID").toString(), org5.get("FULL_NAME").toString()));
                        }
                        //
                        orgClassListLevel3.add(new OrgClass(org4.get("ORGANIZATION_ID").toString(), org4.get("FULL_NAME").toString(), orgClassListLevel4));
                    }
                    //
                    orgClassListLevel2.add(new OrgClass(org3.get("ORGANIZATION_ID").toString(), org3.get("FULL_NAME").toString(), orgClassListLevel3));
                }
                //

                orgClassListLevel1.add(new OrgClass(org.get("ORGANIZATION_ID").toString(), org.get("FULL_NAME").toString(), orgClassListLevel2));
            }

            break;
        }
        //

        return orgClassListLevel1;
    }

    @GetMapping("/showMenuResources")
    public Map showMenuResources(String role, String username, String companyId){

/*        if(StringUtils.isEmpty(role) || StringUtils.isEmpty(username)){
            return null;
        }*/
        //get username through redis using jti of token
        String SourceMenu = systemRepository.GetSourceMenuByUsername(username);
        //String roleTrim = role.substring(5);
        String roleTrim = role;
        String landingPage = "NULL";

        List<Map> res = null;

        if(SourceMenu.equals("ROLE")){
            //res = systemRepository.findByRole(roleTrim); //V1
            res = systemRepository.findByRoleV2(roleTrim, companyId);
        } else {
            res = systemRepository.findByUsername(username);
        }
        // benkuramax sort order for menu Nov11,2022 >>
        res = MenuReOrderBySort(res);
        // == <<
        List<Map> filterREs = res.stream().filter(map -> map.get("VALUE_").toString().equals("/faTracker")).collect(Collectors.toList());

        if(filterREs.size() != 0){
            landingPage = "dashboard";
        } else {
            landingPage = "others";
        }
        // == >>
        boolean isSubmenu = true;

        List<MenuClass> mac = new ArrayList<>();
        MenuClass mc = new MenuClass();
        List<MenuClass> item1 = new ArrayList<>();

        //---------------->>>
        String newlevel = "1";
        String oldllevel = "1";
        String levelStatus = "none";
        int type1 = 0;
        int type2 = 0;
        // --->>
/*        mc = new MenuClass();
        mc.label = "Summary Report";
        item1 = new ArrayList<>();
        item1.add(new MenuClass("FA Tracker", "pi pi-fw pi-home", "/faTracker"));
        mc.items = item1;
        mac.add(mc);*/
        // ---<<
        for(int i = 0; i < res.size(); i++){
            Map map = res.get(i);
            type1 = Integer.parseInt(map.get("TYPE_").toString());

            try{
                Map map2 = res.get(i+1);
                type2 = Integer.parseInt(map2.get("TYPE_").toString());
            }catch (Exception ee){

                if(oldllevel.equals("2") && newlevel.equals("2")){
                    item1.add(new MenuClass(res.get(i).get("PAGES").toString(), res.get(i).get("ICON_URL").toString(), res.get(i).get("VALUE_").toString()));
                }
                type2 = 0;
            }


            if(type1 < type2 ){
                newlevel = type2+"";
                levelStatus = "up";
            }

            if(type1 == type2){
                levelStatus = "mid";
            }

            if(type1 > type2){
                newlevel = type2+"";
                levelStatus = "down";
            }

            if(levelStatus.equals("up")){
                mc = new MenuClass();
                mc.label = map.get("PAGES").toString();

                item1 = new ArrayList<>();

                oldllevel = newlevel;
                continue;
            }

            if(levelStatus.equals("mid")){

                if(oldllevel.equals("1") && newlevel.equals("1")){
                    mc = new MenuClass();
                    mc.label = map.get("PAGES").toString();
                    item1 = new ArrayList<>();
                    item1.add(new MenuClass(res.get(i).get("PAGES").toString(), res.get(i).get("ICON_URL").toString(), res.get(i).get("VALUE_").toString()));
                    mc.items = item1;
                    mac.add(mc);
                } else {

                    item1.add(new MenuClass(res.get(i).get("PAGES").toString(), res.get(i).get("ICON_URL").toString(), res.get(i).get("VALUE_").toString()));
                }


                continue;
            }

            if(levelStatus.equals("down")){

                boolean con = true;

                if(oldllevel.equals("2")  && newlevel.equals("1")){
                    item1.add(new MenuClass(res.get(i).get("PAGES").toString(), res.get(i).get("ICON_URL").toString(), res.get(i).get("VALUE_").toString()));
                }

                if(oldllevel.equals("1")  && newlevel.equals("0")){
                    mc = new MenuClass();
                    mc.label = map.get("PAGES").toString();
                    item1 = new ArrayList<>();
                    item1.add(new MenuClass(res.get(i).get("PAGES").toString(), res.get(i).get("VALUE_").toString(), res.get(i).get("VALUE_").toString()));
                    if(!StringUtils.equalsIgnoreCase("View Application",mc.label)){
                        mc.items = item1;
                    }
                    mac.add(mc);
                    con = false;
                }

                if(oldllevel.equals("1")  && newlevel.equals("1")) con = false;

                if(con) {
                    mc.items = item1;
                    mac.add(mc);
                    oldllevel = newlevel;
                }
                continue;
            }
        }

        //----------------<<<
        Map feedback = new HashMap();
        feedback.put("menu", mac);
        feedback.put("landingpage", landingPage);
        return feedback;
    }

    private List<Map>  MenuReOrderBySort(List<Map> res) {
        List<Map> selSortResource = res.stream().filter(mapp -> mapp.get("TYPE_").toString().equals("1"))

                .collect(Collectors.toList());

        selSortResource.sort(Comparator.comparing(m -> m.get("SORT").toString(),
                Comparator.nullsLast(Comparator.naturalOrder())
        ));

        String[] codes = selSortResource.stream().map(x-> x.get("CODE_").toString()).toArray(String[]::new);

        List<Map> menuOrder = new ArrayList<>();

        for (int i = 0; i < codes.length; i++) {

            final String code = codes[i];
            List<Map> orderRes = res.stream().filter(mapp -> mapp.get("CODE_").toString().startsWith(code))
                    .collect(Collectors.toList());

            menuOrder.addAll(orderRes);

        }

        return menuOrder;
    }

    @GetMapping("/getAccountAccessAreas")
    public List<Map> getAccountAccessAreas(String username) {


        log.info("getAccountAccessAreas by user name: "+username);
        //String fhId = systemRepository.getFhIdByUsername(username);
        List<Map> accountAccess = systemRepository.getAccountAccessAreas(username);

        log.info("accountAccess: "+accountAccess);

        return accountAccess;
    }

    //2015-12-08 00:00:00
    public static boolean isValidDate(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }
    //04/02/2020
    public static boolean isValidDate2(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }
    //19/08/2021
    public static boolean isValidDate3(String inDate){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    //04.26.2018
    public static boolean isValidDate4(String inDate){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    //04-30-2021
    public static boolean isValidDate5(String inDate){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    //12./27/2016
    public static boolean isValidDate6(String inDate){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM./dd/yyyy");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    //2019/02/024
    public static boolean isValidDate7(String inDate){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    //02/15 2020
    public static boolean isValidDate8(String inDate){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd yyyy");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }
    //0911/2021
    public static boolean isValidDate9(String inDate){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMdd/yyyy");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    public String getDateFormatted(String formatDate, String date){
        String strFormat = "";
        try{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatDate);
            Date date1 = simpleDateFormat.parse(date);
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            strFormat = simpleDateFormat1.format(date1);
        }catch (Exception ex){
            log.error("Error: " + ex.getMessage());
        }

        return strFormat;
    }

    public String convertDate(String date){
        String strDate = "";
        if(isValidDate3(date)){
            strDate = getDateFormatted("dd/MM/yyyy",date);
        }else if(isValidDate(date)){
            strDate = getDateFormatted("yyyy-MM-dd HH:mm:ss",date);
        }else if(isValidDate2(date)){
            strDate = getDateFormatted("MM/dd/yyyy",date);
        }else if(isValidDate4(date)){
            strDate = getDateFormatted("MM.dd.yyyy",date);
        }else if(isValidDate5(date)){
            strDate = getDateFormatted("MM-dd-yyyy",date);
        }else if(isValidDate6(date)){
            strDate = getDateFormatted("MM./dd/yyyy",date);
        }else if(isValidDate7(date)){
            strDate = getDateFormatted("yyyy/MM/dd",date);
        }else if(isValidDate8(date)){
            strDate = getDateFormatted("MM/dd yyyy",date);
        }else if(isValidDate9(date)){
            strDate = getDateFormatted("MMdd/yyyy",date);
        }
        return strDate;
    }

    public void addNewDate(String id, String date, String table){
        try{
            log.info("isValidDate3: " + convertDate(date));
            Map param = new HashMap();
            if(table == "sap"){
                param.put("Id", id);
                param.put("issueDate", convertDate(date));
                systemRepository.updateSapIssueDate(param);
            }else if (table == "equipments"){
                param.put("Id", id);
                param.put("DatePurchase", convertDate(date));
                systemRepository.updateTePurchaseDate(param);
            }else if(table == "keeperHistory"){
                param.put("Id", id);
                param.put("issueDate", convertDate(date));
                systemRepository.updateKeeperIssueDate(param);
            }else if(table == "rental"){
                param.put("Id", id);
                param.put("DateDelivered", convertDate(date));
                systemRepository.updateRentalHistoryDate(param);
            }
        }catch (Exception ex){
            log.error("Error: " + ex.getMessage());
        }

    }

    public static Instant getDateFromString(String getDate){
        Instant instant = null;
        instant = Instant.parse(getDate);
        return instant;
    }

    @GetMapping("/updateRantalKeeperDate")
    public Map updateRantalKeeperDate(){
        log.info("updateRantalKeeperDate");
        Map resultMap = new HashMap<>();
        resultMap.put("update_count",0);
        resultMap.put("total_count",0);
        resultMap.put("success",0);
        resultMap.put("error",0);

        try{
            int intIssueDate = 0;
            int nullCount = 0;
            int updateCount = 0;
            int validDateCount = 0;
            int invalidDateCount = 0;
            SerialDate sd = null;
            LocalDateTime dt = null;

            List<Map> sapList = systemRepository.getRentalList();
            resultMap.put("total_count",sapList.size());
            for(int i = 0; i < sapList.size(); i++){
                if(!isValidDate(String.valueOf(sapList.get(i).get("DateDelivered"))) && !isValidDate2(String.valueOf(sapList.get(i).get("DateDelivered")))
                        && !isValidDate3(String.valueOf(sapList.get(i).get("DateDelivered")))){
                    if(sapList.get(i).get("DateDelivered") != null){
                        log.info("ID: "+sapList.get(i).get("Id")+"|| Issue Date: "+sapList.get(i).get("DateDelivered"));

                        intIssueDate = Integer.parseInt(sapList.get(i).get("DateDelivered").toString());
                        sd = new SerialDate(intIssueDate);
                        dt = LocalDateTime.of(
                                LocalDate.ofEpochDay(sd.toEpochDays()),
                                LocalTime.ofSecondOfDay(sd.toDaySeconds()));

                        Map param = new HashMap<>();
                        param.put("Id", sapList.get(i).get("Id"));
                        param.put("DateDelivered", dt);

                        log.info("ID: "+sapList.get(i).get("Id")+"|| Issue Date Revised: "+dt);

                        systemRepository.updateRentalHistoryDate(param);
                        updateCount++;

                        log.info("UPDATE SUCCESS");

                    }else{
                        nullCount++;
                    }
                }else{
                    log.info("valid date");
                    addNewDate(sapList.get(i).get("Id").toString(),sapList.get(i).get("DateDelivered").toString(),"rental");
                    validDateCount++;
                }
                resultMap.put("updateCount",updateCount);
                resultMap.put("null_count",nullCount);
                resultMap.put("validDateCount",validDateCount);
            }

        }catch(Exception e){
            resultMap.put("error",1);
            resultMap.put("error_message",e.getMessage());
        }



        return resultMap;
    }

    @GetMapping("/updateIssuedDateSap")
    public Map updateIssuedDateSap() {

        log.info("updateIssuedDateSap");
        Map resultMap = new HashMap<>();
        resultMap.put("update_count",0);
        resultMap.put("total_count",0);
        resultMap.put("success",0);
        resultMap.put("error",0);

        try{
            int intIssueDate = 0;
            int nullCount = 0;
            int updateCount = 0;
            int validDateCount = 0;
            int invalidDateCount = 0;
            SerialDate sd = null;
            LocalDateTime dt = null;

            List<Map> sapList = systemRepository.getSapList();
            resultMap.put("total_count",sapList.size());
            for(int i = 0; i < sapList.size(); i++){
                if(!isValidDate(String.valueOf(sapList.get(i).get("IssueDate"))) && !isValidDate2(String.valueOf(sapList.get(i).get("IssueDate")))
                        && !isValidDate3(String.valueOf(sapList.get(i).get("IssueDate")))){
                    if(sapList.get(i).get("IssueDate") != null){
                        log.info("ID: "+sapList.get(i).get("Id")+"|| Issue Date: "+sapList.get(i).get("IssueDate"));

                        intIssueDate = Integer.parseInt(sapList.get(i).get("IssueDate").toString());
                        sd = new SerialDate(intIssueDate);
                        dt = LocalDateTime.of(
                                LocalDate.ofEpochDay(sd.toEpochDays()),
                                LocalTime.ofSecondOfDay(sd.toDaySeconds()));

                        Map param = new HashMap<>();
                        param.put("Id", sapList.get(i).get("Id"));
                        param.put("issueDate", dt);

                        log.info("ID: "+sapList.get(i).get("Id")+"|| Issue Date Revised: "+dt);

                        systemRepository.updateSapIssueDate(param);
                        updateCount++;

                        log.info("UPDATE SUCCESS");

                    }else{
                        nullCount++;
                    }
                }else{
                    log.info("valid date");
                    addNewDate(sapList.get(i).get("Id").toString(),sapList.get(i).get("IssueDate").toString(),"sap");
                    validDateCount++;
                }
                resultMap.put("updateCount",updateCount);
                resultMap.put("null_count",nullCount);
                resultMap.put("validDateCount",validDateCount);
            }

        }catch(Exception e){
            resultMap.put("error",1);
            resultMap.put("error_message",e.getMessage());
        }



        return resultMap;
    }

    @GetMapping("/updateIssuedDateKeeperHistory")
    public Map updateIssuedDateKeeperHistory() {

        log.info("updateIssuedDateSap");
        Map resultMap = new HashMap<>();
        resultMap.put("update_count",0);
        resultMap.put("total_count",0);
        resultMap.put("success",0);
        resultMap.put("error",0);

        try{
            int intIssueDate = 0;
            int nullCount = 0;
            int updateCount = 0;
            int validDateCount = 0;
            int invalidDateCount = 0;
            SerialDate sd = null;
            LocalDateTime dt = null;

            List<Map> sapList = systemRepository.getKeeperList();
            resultMap.put("total_count",sapList.size());
            for(int i = 0; i < sapList.size(); i++){
                if(!isValidDate(String.valueOf(sapList.get(i).get("IssueDate"))) && !isValidDate2(String.valueOf(sapList.get(i).get("IssueDate")))
                        && !isValidDate3(String.valueOf(sapList.get(i).get("IssueDate")))){
                    if(sapList.get(i).get("IssueDate") != null){
                        log.info("ID: "+sapList.get(i).get("Id")+"|| Issue Date: "+sapList.get(i).get("IssueDate"));

                        intIssueDate = Integer.parseInt(sapList.get(i).get("IssueDate").toString());
                        sd = new SerialDate(intIssueDate);
                        dt = LocalDateTime.of(
                                LocalDate.ofEpochDay(sd.toEpochDays()),
                                LocalTime.ofSecondOfDay(sd.toDaySeconds()));

                        Map param = new HashMap<>();
                        param.put("Id", sapList.get(i).get("Id"));
                        param.put("issueDate", dt);

                        log.info("ID: "+sapList.get(i).get("Id")+"|| Issue Date Revised: "+dt);

                        systemRepository.updateKeeperIssueDate(param);
                        updateCount++;

                        log.info("UPDATE SUCCESS");

                    }else{
                        nullCount++;
                    }
                }else{
                    log.info("valid date");
                    addNewDate(sapList.get(i).get("Id").toString(),sapList.get(i).get("IssueDate").toString(),"keeperHistory");
                    validDateCount++;
                }
                resultMap.put("updateCount",updateCount);
                resultMap.put("null_count",nullCount);
                resultMap.put("validDateCount",validDateCount);
            }

        }catch(Exception e){
            resultMap.put("error",1);
            resultMap.put("error_message",e.getMessage());
        }



        return resultMap;
    }

    @GetMapping("/updatePurchaseDateTE")
    public Map updateIPurchaseDateTE() {

        log.info("updateIssuedDateTE");
        Map resultMap = new HashMap<>();
        resultMap.put("update_count",0);
        resultMap.put("total_count",0);
        resultMap.put("success",0);
        resultMap.put("error",0);

        try{
            int intPurchaseDate = 0;
            int nullCount = 0;
            int updateCount = 0;
            int validDateCount = 0;
            int invalidDateCount = 0;
            SerialDate sd = null;
            LocalDateTime dt = null;

            List<Map> teList = systemRepository.getTEList();
            resultMap.put("total_count",teList.size());
            for(int i = 0; i < teList.size(); i++){
                if(!isValidDate(String.valueOf(teList.get(i).get("DatePurchase"))) && !isValidDate2(String.valueOf(teList.get(i).get("DatePurchase")))
                        && !isValidDate3(String.valueOf(teList.get(i).get("DatePurchase")))  && !isValidDate4(String.valueOf(teList.get(i).get("DatePurchase")))
                        && !isValidDate5(String.valueOf(teList.get(i).get("DatePurchase"))) && !isValidDate6(String.valueOf(teList.get(i).get("DatePurchase")))
                        && !isValidDate7(String.valueOf(teList.get(i).get("DatePurchase"))) && !isValidDate8(String.valueOf(teList.get(i).get("DatePurchase")))
                        && !isValidDate9(String.valueOf(teList.get(i).get("DatePurchase")))){
                    if(teList.get(i).get("DatePurchase") != null){
                        log.info("ID: "+teList.get(i).get("Id")+"|| Purchase Date: "+teList.get(i).get("DatePurchase"));

                        intPurchaseDate = Integer.parseInt(teList.get(i).get("DatePurchase").toString());
                        sd = new SerialDate(intPurchaseDate);
                        dt = LocalDateTime.of(
                                LocalDate.ofEpochDay(sd.toEpochDays()),
                                LocalTime.ofSecondOfDay(sd.toDaySeconds()));

                        Map param = new HashMap<>();
                        param.put("Id", teList.get(i).get("Id"));
                        param.put("DatePurchase", dt);

                        log.info("ID: "+teList.get(i).get("Id")+"|| Purchase Date Revised: "+dt);

                        systemRepository.updateTePurchaseDate(param);
                        updateCount++;

                        log.info("UPDATE SUCCESS");

                    }else{
                        nullCount++;
                    }
                }else{
                    log.info("valid date");
                    log.error("isValidDate3: " + teList.get(i).get("DatePurchase"));
                    addNewDate(teList.get(i).get("Id").toString(), teList.get(i).get("DatePurchase").toString(),"equipments");
                    validDateCount++;
                }

            }
            resultMap.put("updateCount",updateCount);
            resultMap.put("null_count",nullCount);
            resultMap.put("validDateCount",validDateCount);
        }catch(Exception e){
            resultMap.put("error",1);
            resultMap.put("error_message",e.getMessage());
        }
        return resultMap;
    }

    @GetMapping(value = "/getOrgUserRelation/{id}")
    public List<OrgUserRelationResponse> getUserOrgRelationByAccountId(@PathVariable("id") String accountId){
        return systemRepository.getOrgUserRelation(accountId);
    }

    @PostMapping("/onChangeNewRoleOrg")
    public Map OnChangeNewRoleOrg(@RequestBody Map<String, Object> request){
        String newRoleOrg[] = request.get("newRoleOrg").toString().split("\\:");

        String roleid = newRoleOrg[0];
        String orgid = newRoleOrg[1];
        String userid = request.get("userid").toString();
        Map org_det = systemRepository.GetPathByOrgId(orgid);
        int orgtype = Integer.parseInt(org_det.get("type_").toString());
        String path = MapUtils.getString(org_det, "path_", "");

        Map map = new HashMap();
        map.put("userid", userid);
        map.put("roleid", roleid);
        map.put("orgid", orgid);
        map.put("orgtype", orgtype);
        map.put("companyId", path.split("\\.")[0]);

        switch (orgtype){
            case 1: // deparment
                map.put("projectid", "0");
                map.put("regionid", "0");
                break;
            /*case 2:// project - old
                map.put("projectid", orgid);
                map.put("regionid", "0");
                break;*/
            case 2:// region
                map.put("regionid", orgid);
                map.put("areaid", "0");
                map.put("projectid", "0");
                break;
            /*case 3:// region - old
                map.put("projectid", "0");
                map.put("regionid", orgid);
                break;*/
            case 3:// area
                map.put("regionid", org_det.get("path_").toString().split("\\.")[1]);
                map.put("areaid", orgid);
                map.put("projectid", "0");
                break;
            /*case 4:// area - old

                String[] orgPathSplit = org_det.get("path_").toString().split("\\.");
                map.put("projectid", orgPathSplit[1]);
                map.put("regionid", orgPathSplit[2]);
                break;*/
            case 4:// project
                String[] orgPathSplit = org_det.get("path_").toString().split("\\.");
                map.put("regionid", orgPathSplit[1]);
                map.put("areaid", orgPathSplit[2]);
                map.put("projectid", orgid);
                break;
        }

        log.info("onChangeNewRoleOrg | map: " + new Gson().toJson(map));
        Map feedback = Utils.xo().sync().GetFeedback(
                () -> {
                    systemRepository.SwitchAccountRoleOrg(map);
                }
        );

        feedback.put("roleid", map.get("roleid").toString());
        feedback.put("orgid", map.get("orgid").toString());
        feedback.put("orgtype", orgtype+"");
        feedback.put("projectid", map.get("projectid").toString());
        feedback.put("areaid", map.get("areaid").toString());
        feedback.put("regionid", map.get("regionid").toString());
        feedback.put("companyId", map.get("companyId").toString());

        return feedback;
    }

    @PostMapping("/samplePost")
    public void SamplePost(@RequestBody Map<String, Object> request){
        String qwe = "123";
    }

    @PostMapping("profileLoading")
    public Map ProfileLoading(@RequestBody Map req){

        //String qwe = "123";
        Map feedback = new HashMap();

        Map accountDetails = systemRepository.getAccountById(req);
        List<Map> teKeeperMaterialList = systemRepository.getKeeperMaterialList(req);

        feedback.put("account", accountDetails);
        feedback.put("materials", teKeeperMaterialList);

        return feedback;
    }


}