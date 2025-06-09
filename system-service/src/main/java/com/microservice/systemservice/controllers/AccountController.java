package com.microservice.systemservice.controllers;

import com.code.share.codesharing.excel.UploadUserExcelHelper;
import com.code.share.codesharing.excel.model.UserAccountExcel;
import com.code.share.codesharing.tools.Systemm;
import com.code.share.codesharing.tools.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.gson.Gson;
import com.microservice.systemservice.models.*;
import com.microservice.systemservice.repository.AccountRepository;
import com.microservice.systemservice.repository.SystemRepository;
import com.microservice.systemservice.services.ActivityLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/system/account")
@RequiredArgsConstructor
@Slf4j
public class AccountController {
    //private final SystemRepository systemRepository;
    @Autowired
    private ActivityLogService activityLogService;
    @Autowired
    private AccountRepository accountRepository;
    @Value("${Account.signaturePath}")
    String signatureUploadPath;

    @Value("C:/MicroserviceFiles/Account/")
    String accountExcelPath;

    @GetMapping("showAllAccounts")
    public Map showAllAccounts(){

        //List<Map> resAccounts = Util.me.GetData("showAllAccounts", systemRepository.showAllAccounts());
        List<Map> resAccounts = accountRepository.showAllAccounts();
        //List<Map> resRoles = Util.me.GetData("showAllRoles", systemRepository.showAllRoles());
        List<Map> resRoles =  accountRepository.showAllRoles();
        resRoles = resRoles.stream().filter(map -> map.get("STATE").toString().equals("1"))
                .collect(Collectors.toList());

        //List<Map> resOrg = Util.me.GetData("showOrganizationTree", systemRepository.showOrganizationTree());
        List<Map> resOrg = accountRepository.showOrganizationTree();

        String[] parentIds = resOrg.stream().map(x-> x.get("PARENT_ID").toString()).toArray(String[]::new);
        LinkedHashSet<String> lhSetColors = new LinkedHashSet<String>(Arrays.asList(parentIds));
        String[] parentOrgId = lhSetColors.toArray(new String[ lhSetColors.size() ]);
        List<OrgClass> orgClassListLevel1 = new ArrayList<>();
        orgClassListLevel1 = CreateOrganizationTree(resOrg, parentOrgId);

        Map retMap = new HashMap();

        retMap.put("queryAccounts", resAccounts);
        retMap.put("queryRoles", resRoles);
        retMap.put("queryOrgTree", orgClassListLevel1);
        retMap.put("queryOrganization", resOrg);

        return retMap;
    }

    @PostMapping("showAllAccountsByOrg")
    public Map showAllAccounts(@RequestBody GetResponse response){

        Object obj = response.getObject();
        ObjectMapper oMapper = new ObjectMapper();
        Map userInfo = oMapper.convertValue(obj, Map.class);


        //List<Map> resAccounts = Util.me.GetData("showAllAccounts", systemRepository.showAllAccounts());
        //List<Map> resAccounts = accountRepository.showAllAccounts();
        List<Map> resAccounts = accountRepository.showAllAccountsByOrg(userInfo);
        //List<Map> resRoles = Util.me.GetData("showAllRoles", systemRepository.showAllRoles());
        List<Map> resRoles =  accountRepository.showAllRoles();
        resRoles = resRoles.stream().filter(map -> map.get("STATE").toString().equals("1"))
                .collect(Collectors.toList());

        //List<Map> resOrg = Util.me.GetData("showOrganizationTree", systemRepository.showOrganizationTree());
        List<Map> resOrg = accountRepository.showOrganizationTree();

        String[] parentIds = resOrg.stream().map(x-> x.get("PARENT_ID").toString()).toArray(String[]::new);
        LinkedHashSet<String> lhSetColors = new LinkedHashSet<String>(Arrays.asList(parentIds));
        String[] parentOrgId = lhSetColors.toArray(new String[ lhSetColors.size() ]);
        List<OrgClass> orgClassListLevel1 = new ArrayList<>();
        orgClassListLevel1 = CreateOrganizationTree(resOrg, parentOrgId);

        Map retMap = new HashMap();

        retMap.put("queryAccounts", resAccounts);
        retMap.put("queryRoles", resRoles);
        retMap.put("queryOrgTree", orgClassListLevel1);
        retMap.put("queryOrganization", resOrg);

        return retMap;
    }

    @RequestMapping(value = "/accountsUpdate", method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Map accountsUpdate(MultipartHttpServletRequest request) {
        boolean statusGood = true;

        try {
            String objectStr = request.getParameter("object");
            Map map = StringUtils.isNotBlank(objectStr) ? new ObjectMapper().readValue(objectStr, Map.class) : new HashMap<>();
            if (!map.containsKey("ACCOUNT_NAME") || !map.containsKey("role_id") || !map.containsKey("orgid") || !map.containsKey("FULL_NAME")) {
                statusGood = false;
            }
            if (statusGood) {
                List<MultipartFile> signature = request.getFiles("signature");
                String action = request.getParameter("action");

                String userId = request.getParameter("userId");
                String userFullName = request.getParameter("userFullName");

                if (!action.equals("add")) {
                    if (CollectionUtils.isNotEmpty(signature)) {
                        String sigPath = this.saveSignature(signature.get(0), MapUtils.getString(map, "ACCOUNT_ID"));
                        map.put("SIGNATURE", sigPath);
                    }

                    accountRepository.DeleteRoleOrgByAccountId(map);
                    processRoleOrgList(map);
                    Map mapMainParams = generateRoleOrgDesignation(map);
                    accountRepository.updateAccountMainRoleOrgSetup(mapMainParams);
                    accountRepository.UpdateAccountByUserId(map);

                    activityLogService.logActivity("Account Management", "Account Update", userId, userFullName);
                } else {
                    Map mapMainParams = generateRoleOrgDesignation(map);
                    map.put("relOrgData", mapMainParams);
                    accountRepository.AddNewAccount(map);

                    activityLogService.logActivity("Account Management", "Account Creation", userId, userFullName);

                    if (CollectionUtils.isNotEmpty(signature)) {
                        String sigPath = this.saveSignature(signature.get(0), MapUtils.getString(map, "ACCOUNT_ID"));
                        map.put("SIGNATURE", sigPath);
                        accountRepository.updateAccountSignature(map);
                    }
                    processRoleOrgList(map);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map feedback = new HashMap();
        if (statusGood) {
            feedback.put("feedback", "1");
        } else {
            feedback.put("feedback", "0");
        }
        return feedback;
    }



    @PostMapping("/accountActivation")
    public void accountActivation(@RequestBody GetResponse response){

        Object obj = response.getObject();
        ObjectMapper oMapper = new ObjectMapper();
        Map map = oMapper.convertValue(obj, Map.class);

        String state = map.get("STATE").toString();
        state = state.equals("1") ? "0": "1" ;

        map.put("STATE",state);
        accountRepository.UpdateAccountStateById(map);
        //Util.me.DeleteFile("showAllAccounts");

        String createdById = response.getUserId();
        String createdByName = response.getUserFullName();
        int st = Integer.parseInt(state);
        String actions = "";
        if (st == 1){
            actions = "Enable Account";
        } else {
            actions = "Disable Account";
        }
        activityLogService.logActivity("Account Management", actions, createdById, createdByName);


    }

    @PostMapping("/SwitchSourceMenuSave")
    public void switchSourceMenuSave(@RequestBody GetResponse response) {

        Object obj = response.getObject();
        ObjectMapper oMapper = new ObjectMapper();
        Map map = oMapper.convertValue(obj, Map.class);

        String selectedSourceMenu = response.getStr();
        String UserId = map.get("ACCOUNT_ID").toString();

        Map map01 = new HashMap();
        map01.put("UserId", UserId);
        map01.put("SourceMenu", selectedSourceMenu);

        accountRepository.SwitchSourceMenubyId(map01);

        String createdById = response.getUserId();
        String createdByName = response.getUserFullName();
        String actions = "";
        if (selectedSourceMenu == "ROLE"){
            actions = "Change Source Menu to Role";
        }
        else{
            actions = "Change Source Menu to User";
        }

        activityLogService.logActivity("Account Management", actions, createdById, createdByName);

        //String qwer = "123";
    }

    @GetMapping("/viewSignature")
    public void viewSignature(HttpServletRequest request, HttpServletResponse response, String signaturePath) throws IOException {
        Utils.xo().syst().FileViewerFunction(request,
                response,
                signatureUploadPath,
                signaturePath,
                Systemm.FileType.Image);
    }

    @PostMapping("/editUserPassword")
    public Map EditUserPassword(@RequestBody Map<String, Object> requestMap){

       /* char[] passwordChars = new char[str.length()];
        for (int i = 0; i < str.length(); i++) {
            passwordChars[i] = str.charAt(i);
        }
        String hashed = BCrypt.hashpw(String.valueOf(passwordChars), BCrypt.gensalt(12));*/
        /*Map feedback = Sync.GetFeedback(

                () -> {
                    systemRepository.updateNewUserPassword(requestMap);
                }
        );*/
        String pageName = (String) requestMap.get("pageName");

        Map feedback = Utils.xo().sync().GetFeedback(() -> {
            String str = (String) requestMap.get("password");
            String createdById = requestMap.get("userId").toString();
            String createdByName = requestMap.get("userFullName").toString();
            String hashed = Utils.xo().conv().ConvertPasswordToBCryptHash(str);
            requestMap.put("password", hashed);
            accountRepository.updateNewUserPassword(requestMap);
            activityLogService.logActivity(pageName, "Change Password", createdById, createdByName);
        });

        /*Utils.xo().sync().GetTest(
                (hl) -> {
                    hl.toUpperCase();
                }
        );*/

        return feedback;
    }

    @PostMapping("/getRoleOrgListByUserid")
    public List<Map> GetRoleOrgListByUserid (@RequestBody Map<String, Object> request){

        String userid = request.get("userid").toString();

        List<Map> roleOrgCont = accountRepository.GetRoleOrgListByUserid(userid);

        return roleOrgCont;

    }
    // FUNTIONS ============================================================================>>
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
    private void processRoleOrgList(Map map) {
        List<Map> roleOrgList =  (List<Map>) map.get("roleOrgList");

        for(Map mapRO : roleOrgList){
            String name = mapRO.get("name").toString();
            String[] roleOrg = mapRO.get("value").toString().split(":");

            Map mapParams = new HashMap();
            try {
                mapParams.put("userid", map.get("ACCOUNT_ID").toString());
            } catch (Exception e) { }
            mapParams.put("roleid", roleOrg[0]);
            mapParams.put("orgid", roleOrg[1]);
            //systemRepository.InsertNewRoleOrgByAccount(mapParams);
            accountRepository.InsertNewRoleOrgByAccount(mapParams);
        }
    }
    private Map generateRoleOrgDesignation(Map map) {

        int orgtype = Integer.parseInt(map.get("orgtype").toString());
        String orgPath = map.get("orgpath").toString();
        String[] roleOrg = map.get("roleOrgMain").toString().split(":");
        String roleid = roleOrg[0];
        String orgid = roleOrg[1];
        // ======<
        Map mapMainParams = new HashMap();
        try {
            mapMainParams.put("userid", map.get("ACCOUNT_ID").toString());
        } catch (Exception e) { }
        mapMainParams.put("roleid", roleid);
        mapMainParams.put("orgid", orgid);
        mapMainParams.put("orgtype", orgtype);
        mapMainParams.put("companyId", orgPath.split("\\.")[0]);

        switch (orgtype){
            case 1: // deparment
                mapMainParams.put("regionid", "0");
                mapMainParams.put("areaid", "0");
                break;
            case 2:// region
                mapMainParams.put("regionid", orgid);
                mapMainParams.put("areaid", "0");
                break;
            case 3:// area
                String[] orgPathSplit2 = orgPath.split("\\.");
                mapMainParams.put("regionid", orgPathSplit2[1]);
                mapMainParams.put("areaid", orgid);
                break;
            case 4:// project
                String[] orgPathSplit = orgPath.split("\\.");
                mapMainParams.put("regionid", orgPathSplit[1]);
                mapMainParams.put("areaid", orgPathSplit[2]);
                break;
        }

        /*switch (orgtype){ // old codes
            case 1: // deparment
                mapMainParams.put("projectid", "0");
                mapMainParams.put("regionid", "0");
                break;
            case 2:// project -- region
                mapMainParams.put("projectid", orgid);
                mapMainParams.put("regionid", "0");
                break;
            case 3:// region -- area
                mapMainParams.put("projectid", "0");
                mapMainParams.put("regionid", orgid);
                break;
            case 4:// area -- project
                String[] orgPathSplit = orgPath.split("\\.");
                mapMainParams.put("projectid", orgPathSplit[1]);
                mapMainParams.put("regionid", orgPathSplit[2]);
                break;
        }*/

        return mapMainParams;
    }

    /**
     * Save signature image to file system
     * @param signature
     * @param accountId
     * @return File path of the signature (Excluding the base signature directory)
     */
    public String saveSignature(MultipartFile signature, String accountId) throws IOException {
        if (signature == null) return null;

        LocalDate currentDate = LocalDate.now();
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();
        int day = currentDate.getDayOfMonth();
        String fileExtension = signature.getOriginalFilename()
                .substring(signature.getOriginalFilename().lastIndexOf(".") + 1);

        //User's signature directory
        String fileDir = year + "/" + month + "/" + day + "/" + accountId + "/";
        String fileName = "sig_" + accountId + "." + fileExtension;

        Path path = Paths.get(signatureUploadPath + fileDir + fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, signature.getBytes());

        return fileDir + fileName;
    }

    @PostMapping(value = "/uploadNewUserFromExcel", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResultMsg<List<Map<String, Object>>> UploadNewUserFromExcel(MultipartHttpServletRequest multipartFile, @RequestParam Map<String, String> body, HttpServletRequest request) throws IOException {

        ResultMsg<List<Map<String, Object>>> result = new ResultMsg<>();
        List<Map<String, Object>> resultList = new ArrayList<>();

        AtomicInteger insertCount = new AtomicInteger();
        AtomicInteger duplicateCount = new AtomicInteger();

        String username = body.get("username");
        String createdById = body.get("userId");
        String createdByName = body.get("userFullName");

        List<MultipartFile> files = multipartFile.getFiles("excel[]");
// Saving excel into local storage >>
        Object[] res = Utils.xo().syst().FileSaving(accountExcelPath, files.get(0),"/UploadAccountExcel");
// Inserting data into database >>
        File file = new File(accountExcelPath + res[0].toString());
// Populate data into Excel helper
        UploadUserExcelHelper excelHelper = new UploadUserExcelHelper();
        excelHelper.setFile(file);

        com.code.share.codesharing.excel.model.ResultMsg<Boolean> validation = excelHelper.validate();

        if(!validation.isSuccess() || !validation.getData()){
            resultList.add(Map.of(
                            "file", files.get(0).getOriginalFilename() ,
                            "status", "Fail",
                            "remarks", validation.getMessage(),
                            "insertCount",0,
                            "duplicateCount", 0
                    )
            );
            return result;
        }

        List<UserAccountExcel> userAccountList = excelHelper.read();

        //String[] accountNameList = accountRepository.getAllAccountNames().split(",");
        List<String> accountNameList = Arrays.asList(accountRepository.getAllAccountNames().split(","));

// Data adjustment >>
// Inserting data into database >>
        for(UserAccountExcel userAccount : userAccountList){
            //
            if(accountNameList.stream().anyMatch(userAccount.getAccountName() :: equals)){
                duplicateCount.getAndIncrement();
                continue;
            }

            accountRepository.InsertNewUserAccount(userAccount);
            //
            Map orgQuery = accountRepository.getOrganizatioById(userAccount.getOrganizationId());
            Map accUpdate = manageRoleOrgIds(orgQuery, userAccount);
            accountRepository.updateAccountMainRoleOrgSetup(accUpdate);
            //
            Map mapDel = new HashMap();
            mapDel.put("ACCOUNT_ID", userAccount.getNEW_ID());
            accountRepository.DeleteRoleOrgByAccountId(mapDel);
            //
            Map mapInsRoleOrg = new HashMap();
            mapInsRoleOrg.put("userid", userAccount.getNEW_ID());
            mapInsRoleOrg.put("roleid", userAccount.getRoleId());
            mapInsRoleOrg.put("orgid", userAccount.getOrganizationId());
            accountRepository.InsertNewRoleOrgByAccount(mapInsRoleOrg);

            insertCount.getAndIncrement();

        }

        activityLogService.logActivity("Account Management", "Imported Accounts", createdById, createdByName);

        excelHelper.clear();

        resultList.add(Map.of(
                        "file", files.get(0).getOriginalFilename() ,
                        "status", "Success",
                        "insertCount", insertCount.intValue(),
                        "duplicateCount", duplicateCount.intValue()
                )
        );
        result.setSuccess(true);
        result.setData(resultList);

        return result;

    }

    private Map manageRoleOrgIds(Map orgQuery, UserAccountExcel userAccount) {
        Map accUpdate = new HashMap();
        String userid = userAccount.getNEW_ID(); accUpdate.put("userid",userid);
        String orgid = userAccount.getOrganizationId(); accUpdate.put("orgid", orgid);
        String roleid = userAccount.getRoleId(); accUpdate.put("roleid", roleid);

        int orgType_ = Integer.parseInt( orgQuery.get("type_").toString());
        accUpdate.put("orgtype", orgType_);
        String orgPath_ = orgQuery.get("path_").toString();

        switch (orgType_){
            case 1: // deparment
                accUpdate.put("projectid", "0");
                accUpdate.put("regionid", "0");
                break;
            case 2:// project
                accUpdate.put("projectid", orgid);
                accUpdate.put("regionid", "0");
                break;
            case 3:// region
                accUpdate.put("projectid", "0");
                accUpdate.put("regionid", orgid);
                break;
            case 4:// area
                String[] orgPathSplit = orgPath_.split("\\.");
                accUpdate.put("projectid", orgPathSplit[1]);
                accUpdate.put("regionid", orgPathSplit[2]);
                break;
        }

        return accUpdate;
    }

    @GetMapping("getOrgTreeAndList")
    public Map getOrgTreeAndList() {
        Map retMap = new HashMap();
        List<Map> resOrg = accountRepository.showOrganizationTree();
        String[] parentIds = resOrg.stream().map(x-> x.get("PARENT_ID").toString()).toArray(String[]::new);
        LinkedHashSet<String> lhSetColors = new LinkedHashSet<String>(Arrays.asList(parentIds));
        String[] parentOrgId = lhSetColors.toArray(new String[ lhSetColors.size() ]);
        List<OrgClass> orgClassListLevel1 = CreateOrganizationTree(resOrg, parentOrgId);
        retMap.put("queryOrgTree", orgClassListLevel1);
        retMap.put("queryOrganization", resOrg);
        return retMap;
    }

    @PostMapping("/getWidgetPermission")
    public Map GetWidgetPermission(@RequestBody Map<String, Object> mapReq){

        Map feedback = new HashMap();

        //String source_menu = teLedgerRepository.getUserSourceMenuByUserId(mapReq);
        mapReq.put("sourcemenu", MapUtils.getString(mapReq, "sourcemenu"));

        Map widgetList = accountRepository.getWidgetPermissionAcc(mapReq);
        Gson gson = new Gson();

        if (widgetList != null) {
            feedback = (Map) gson.fromJson(widgetList.get("json_widget").toString(), feedback.getClass());
        }

        return feedback;
    }

}