package com.microservice.systemservice.controllers;

import com.microservice.systemservice.models.DataDictionary;
import com.microservice.systemservice.models.PageResult;
import com.microservice.systemservice.models.ResultMsg;
import com.microservice.systemservice.models.*;
import com.microservice.systemservice.repository.DataDictionaryRepository;
import com.microservice.systemservice.services.ActivityLogService;
import com.microservice.systemservice.services.DataDictionaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/te-datadict-list/list")
@Slf4j
public class DataDictionaryController {

    @Autowired
    DataDictionaryService dataDService;

    @Autowired
    ActivityLogService activityLogService;

    @Autowired
    DataDictionaryRepository dataDRepository;

    @GetMapping("/get-dListFilterValues")
    public Map<String,Object> getDDFilterValues(){
        List<Map<String,Object>> dListFilterCategories = dataDRepository.getDDListFilterValues();

        List<Map<String,Object>> categories = dListFilterCategories.stream()
                .filter(x -> MapUtils.getString(x, "code", "").equals("1"))
                .collect(Collectors.toList());
        List<Map<String,Object>> teNames = dListFilterCategories.stream()
                .filter(x -> MapUtils.getString(x, "code", "").equals("2"))
                .collect(Collectors.toList());
        List<Map<String,Object>> brands = dListFilterCategories.stream()
                .filter(x -> MapUtils.getString(x, "code", "").equals("3"))
                .collect(Collectors.toList());
        List<Map<String,Object>> models = dListFilterCategories.stream()
                .filter(x -> MapUtils.getString(x, "code", "").equals("5"))
                .collect(Collectors.toList());
        List<Map<String,Object>> itemCodes = dListFilterCategories.stream()
                .filter(x -> MapUtils.getString(x, "code", "").equals("6"))
                .collect(Collectors.toList());

        Map<String,Object> result = new HashMap<>();
        result.put("categories", categories);
        result.put("teNames", teNames);
        result.put("brands", brands);
        result.put("models", models);
        result.put("itemCodes", itemCodes);

        return result;
    }

    @PostMapping("/te-specification-list")
    public PageResult<DataDictionary> getTEDDList(@RequestBody Map<String, Object> param) {
        try {
            return new PageResult<>(dataDService.getTEDDList(param),
                    dataDService.getTEDDListCount(param),
                    (int) param.get("limit"));
        } catch (Exception ex) {
            log.info("getTEListDataReport.e: " + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(ex));
            return new PageResult<>();
        }
    }
    @PostMapping("/te-attributed-list")
    public PageResult<DataDictionary> getAttibutedList(@RequestBody Map<String, Object> param, HttpServletRequest request) {
        try {
            System.out.println(param);
            return new PageResult<>(dataDService.getAttributedList(param),
                    dataDService.getAttributedListCount(param),
                    (int) param.get("limit"));
        } catch (Exception ex) {
            log.info("getTEListDataReport.e: " + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(ex));
            return new PageResult<>();
        }
    }

    @PostMapping("/te-codes")
    public Map getCodes() {
        Map codeMap = new HashMap();
        List<Map> codeNames = dataDRepository.getCodeNames();
        List<Map> data = dataDRepository.getData();
        codeMap.put("codeNames",codeNames);
        codeMap.put("data",data);
        return codeMap;
    }

    @GetMapping("/te-category-codes")
    public Map getCategoryCodes(){


        List<Map> categoryList = dataDRepository.showCategoryTree();
        List<Map> categoryDataList = dataDRepository.getCategoryChild();

        //==================================================
        List<OrgClass> CategoriesList = new ArrayList<>();
        CategoriesList = PopulateCategoryToTree(categoryList);
        //==================================================

        Map feedback = new HashMap();

        feedback.put("categTree", CategoriesList);
        feedback.put("categData",categoryDataList);

        return feedback;
    }

    private List<OrgClass> PopulateCategoryToTree(List<Map> categoryList) {

        List<OrgClass> RootLevel0 = new ArrayList<>();
        // ===========================================
        List<Map> CategoryList = categoryList.stream().filter(map -> map.get("code").toString().equals("1"))
                .collect(Collectors.toList());
        // LEVEL 1
        List<OrgClass> CategoryLevel1 = new ArrayList<>();

        for(Map categoryNames: CategoryList ){
            List<Map> TENameList = categoryList.stream().filter(map -> map.get("code").toString().equals("2"))
                    .collect(Collectors.toList());
            List<Map> TENameListFilter = TENameList.stream().filter(map -> map.get("parent_id").toString().equals(categoryNames.get("id").toString()))
                    .collect(Collectors.toList());
            TENameListFilter.sort(Comparator.comparing(m -> (String)m.get("name")));
            // LEVEL 2
            List<OrgClass> BrandLevel2 = new ArrayList<>();
            for(Map TEnames: TENameListFilter){
                //
                List<Map> BrandList = categoryList.stream().filter(map -> map.get("code").toString().equals("3"))
                        .collect(Collectors.toList());
                List<Map> BrandListFilter = BrandList.stream().filter(map -> map.get("parent_id").toString().equals(TEnames.get("id").toString()))
                        .collect(Collectors.toList());
                BrandListFilter.sort(Comparator.comparing(m -> (String)m.get("name")));
                // LEVEL 3
                List<OrgClass> BrandLevel3 = new ArrayList<>();
                for(Map brands: BrandListFilter){
                    //
                    List<Map> ModelList = categoryList.stream().filter(map -> map.get("code").toString().equals("5"))
                            .collect(Collectors.toList());
                    List<Map> ModelListFilter = ModelList.stream().filter(map -> map.get("parent_id").toString().equals(brands.get("id").toString()))
                            .collect(Collectors.toList());
                    // LEVEL 4
                    List<OrgClass> ModelLevel4 = new ArrayList<>();
                    for(Map models: ModelListFilter){
                        ModelLevel4.add(new OrgClass(models.get("id").toString(), models.get("name").toString()));
                    }
                    //
                    BrandLevel3.add(new OrgClass(brands.get("id").toString(), brands.get("name").toString(), ModelLevel4));
                }
                BrandLevel2.add(new OrgClass(TEnames.get("id").toString(), TEnames.get("name").toString(), BrandLevel3));
            }
            // <<<[LEVEL 2]
            CategoryLevel1.add(new OrgClass(categoryNames.get("id").toString(), categoryNames.get("name").toString(), BrandLevel2));
        }// <<<[LEVEL 1]
        // ===========================================
        RootLevel0.add(new OrgClass("0", "Categories", CategoryLevel1));
        return RootLevel0;
    }

    @PostMapping("/specificationCreate")
    public ResultMsg<?> specificationCreate(@RequestBody Map<String, Object> body) {
        try {
            dataDService.specificationCreate(body);
            String createdById = body.get("createdById").toString();
            String createdByName = body.get("createdBy").toString();
            activityLogService.logActivity("Data Dictionary", "Added New Specification", createdById, createdByName);
            return new ResultMsg<>( "Specification created successfully");
        } catch (Exception e) {
            log.info("createTE.e: " + ExceptionUtils.getStackTrace(e));
            return new ResultMsg<>(false, "Specification creation failed");
        }
    }

    @PostMapping("/specificationUpdate")
    public ResultMsg<?> specificationUpdate(@RequestBody Map<String, Object> bodyEdit) {
        try {
            dataDService.specificationUpdate(bodyEdit);
            String createdById = bodyEdit.get("updatedById").toString();
            String createdByName = bodyEdit.get("updatedBy").toString();
            activityLogService.logActivity("Data Dictionary", "Updated Specification", createdById, createdByName);
            return new ResultMsg<>( "Specification updated successfully");
        } catch (Exception e) {
            log.info("createTE.e: " + ExceptionUtils.getStackTrace(e));
            return new ResultMsg<>(false, "Specification update failed");
        }
    }

    @PostMapping("/specificationDelete")
    public ResultMsg<?> specificationDelete(@RequestBody Map<String, Object> bodyDelete) {
        try {
            dataDRepository.specificationDelete(bodyDelete);
            String createdById = bodyDelete.get("updatedById").toString();
            String createdByName = bodyDelete.get("updatedBy").toString();
            activityLogService.logActivity("Data Dictionary", "Deleted Specification", createdById, createdByName);
            return new ResultMsg<>( "Specification updated successfully");
        } catch (Exception e) {
            log.info("createTE.e: " + ExceptionUtils.getStackTrace(e));
            return new ResultMsg<>(false, "Specification update failed");
        }
    }


    @PostMapping(value = "/te-submitForm")
    public ResponseEntity<?> crudAttributed(@RequestBody Map<String, Object> param, HttpServletRequest request){
        System.out.println("crudAttributed");
        ResultMsg response = new ResultMsg();
        try{
            if ("".equals(param.get("instanceId"))) {
                param.put("instanceId", null);
            }

            if(MapUtils.getString(param, "dataDictID") == null){
                param.put("action", "create");
                dataDRepository.addForm(param);
                String createdById = param.get("createdById").toString();
                String createdByName = param.get("createdBy").toString();
                activityLogService.logActivity("Data Dictionary", "Added Item Code", createdById, createdByName);
                response.setMessage("Successfully inserted Attribute");
            } else {
                param.put("action", "update");
                dataDRepository.editForm(param);
                String createdById = param.get("createdById").toString();
                String createdByName = param.get("createdBy").toString();
                activityLogService.logActivity("Data Dictionary", "Updated Item Code", createdById, createdByName);
                response.setMessage("Successfully updated Attribute");
            }
            response.setSuccess(true);

            param.put("ip", request.getRemoteAddr());
            param.put("auth", request.getHeader("Authorization"));
            this.updateTeLedgerServiceCategory(param);
        }catch(Exception e ){
            log.error(e.getMessage());
            response.setSuccess(false);
            response.setMessage(e.getLocalizedMessage());
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Calls an API on the te-ledger-service to update the CategoriesUtil
     */
    public ResultMsg<?> updateTeLedgerServiceCategory(Map<String, Object> param) {
        String url = String.format("http://%s:8090/te-ledger/upload/updateCategoriesUtil",
                MapUtils.getString(param, "ip"));
        String auth = MapUtils.getString(param, "auth");

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (StringUtils.isNotBlank(auth)) {
            headers.add(HttpHeaders.AUTHORIZATION, auth);
            headers.add(HttpHeaders.COOKIE, "logged_in=true");
        }
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(param, headers);
        HttpMethod httpMethod = HttpMethod.POST;

        // Call the API using RestTemplate
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ResultMsg<?>> response = restTemplate.exchange(
                url,
                httpMethod,
                requestEntity,
                new ParameterizedTypeReference<>() {
                }
        );

        // Process the response
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            // Handle the error
            return new ResultMsg<>(false, "Error " + response.getStatusCode().value());
        }
    }

    @PostMapping("/att-submitForm")
    public void AttributeDataManager(@RequestBody Map<String, Object> body){
        String qwer = "123";

        if(StringUtils.isEmpty(MapUtils.getString(body, "categId"))){
            dataDRepository.addAttForm(body);
        }
    }

    @PostMapping("/cl-submitForm")
    public void CategoriesDataManager(@RequestBody Map<String, Object> body){
        Map data = new HashMap();

        data.put("updatedDate",body.get("createdDate"));
        data.put("updatedBy",body.get("accountName"));
        data.put("updatedById",body.get("accounId"));

        if("add".equals(body.get("type").toString())){
            data.put("name",body.get("name"));
            data.put("code",body.get("codeToAdd"));
            data.put("parentid",body.get("parentId"));
            data.put("instanceid",body.get("0"));
            data.put("createdDate",body.get("createdDate"));
            data.put("createdBy",body.get("accountName"));
            data.put("createdById",body.get("accounId"));
            dataDRepository.addAttForm(data);
            String createdById = body.get("accounId").toString();
            String createdByName = body.get("accountName").toString();
            String actions = "";
            int codeType = Integer.valueOf(body.get("codeToAdd").toString());
            if (codeType == 2) {
                actions = "Added New TE";
            } else if (codeType == 3){
                actions = "Added New Brand";
            } else if (codeType == 5){
                actions = "Added New Model";
            } else{
                actions = "Added New Category";
            }
            activityLogService.logActivity("Data Dictionary", actions, createdById, createdByName);


        } else {
            data.put("updateName",body.get("updateName"));
            data.put("updateId",body.get("updateId"));
            dataDRepository.editAttForm(data);
            String createdById = body.get("accounId").toString();
            String createdByName = body.get("accountName").toString();
            String actions = "";
            int codeType = Integer.valueOf(body.get("nameCode").toString());
            if (codeType == 2) {
                actions = "Updated TE";
            } else if (codeType == 3){
                actions = "Updated Brand";
            } else if (codeType == 5){
                actions = "Updated Model";
            } else {
                actions = "Updated Category";
            }
            activityLogService.logActivity("Data Dictionary", actions, createdById, createdByName);

        }

    }

    private List<OrgClass> CreateOrganizationTree(List<Map> res, String[] parentOrgId) {

        List<OrgClass> orgClassListLevel1 = new ArrayList<>();

        for(String parentID: parentOrgId){ // LEVEL 1
            List<Map> mapFillFilter = res.stream().filter(map -> map.get("parent_id").toString().equals(parentID))
                    .collect(Collectors.toList());

            for(Map org : mapFillFilter){ // LEVEL 2
                //
                List<Map> mapFilllevel2 = res.stream().filter(map -> map.get("parent_id").toString().equals(org.get("instance_id").toString()))
                        .collect(Collectors.toList());
                mapFilllevel2.sort(Comparator.comparing(m -> (String)m.get("name")));

                List<OrgClass> orgClassListLevel2 = new ArrayList<>();

                for(Map org3: mapFilllevel2){ // LEVEL 3
                    //
                    List<Map> mapFilllevel3 = res.stream().filter(map -> map.get("parent_id").toString().equals(org3.get("instance_id").toString()))
                            .collect(Collectors.toList());
                    mapFilllevel3.sort(Comparator.comparing(m -> (String)m.get("name")));

                    List<OrgClass> orgClassListLevel3 = new ArrayList<>();

                    for(Map org4: mapFilllevel3){ // LEVEL 4
                        //
                        List<Map> mapFilllevel4 = res.stream().filter(map -> map.get("parent_id").toString().equals(org4.get("instance_id").toString()))
                                .collect(Collectors.toList());
                        mapFilllevel4.sort(Comparator.comparing(m -> (String)m.get("name")));

                        List<OrgClass> orgClassListLevel4 = new ArrayList<>();

                        for(Map org5: mapFilllevel4) { // LEVEL 5
                            orgClassListLevel4.add(new OrgClass(org5.get("instance_id").toString(), org5.get("name").toString()));
                        }
                        //
                        orgClassListLevel3.add(new OrgClass(org4.get("instance_id").toString(), org4.get("name").toString(), orgClassListLevel4));
                    }
                    //
                    orgClassListLevel2.add(new OrgClass(org3.get("instance_id").toString(), org3.get("name").toString(), orgClassListLevel3));
                }
                //

                orgClassListLevel1.add(new OrgClass(org.get("instance_id").toString(), org.get("name").toString(), orgClassListLevel2));
            }

            break;
        }
        //

        return orgClassListLevel1;
    }

}
