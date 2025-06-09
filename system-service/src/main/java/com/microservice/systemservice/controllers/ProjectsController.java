package com.microservice.systemservice.controllers;

import com.code.share.codesharing.utils.ExcelExportIns;
import com.microservice.systemservice.dto.OrganizationResponseDTO;
import com.microservice.systemservice.models.Projects;
import com.microservice.systemservice.models.ResultMsg;
import com.microservice.systemservice.repository.ProjectsRepository;
import com.microservice.systemservice.services.ProjectsService;
import com.microservice.systemservice.utils.OrganizationBean;
import com.microservice.systemservice.utils.UserBean;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
@Slf4j
public class ProjectsController {

    private final ProjectsService projectsService;
    private final ProjectsRepository projectsRepository;

    @GetMapping(value = "/loadProjectList")
    public ResponseEntity<?> getAllProjectList(){

        List<Projects> projectsResponses = projectsService.getAllProjectList();
        return new ResponseEntity<>(projectsResponses, HttpStatus.OK);
    }

    @GetMapping("/getAllRegions")
    public List<Map<String,Object>> getAllRegions(){
        return projectsRepository.getAllRegions();
    }

    @RequestMapping(value = "/getOrganizationListTree")
    public List<OrganizationResponseDTO> getOrganizationListTree(){
        return OrganizationBean.getOrganizationTree();
    }

    @RequestMapping(value = "/getLedgerOrganizationListTree")
    public List<OrganizationResponseDTO> getLedgerOrganizationListTree(){

        return OrganizationBean.getLedgerOrganizationTree();
    }

    @RequestMapping(value = "/getLedgerOrganizationListAreaTree")
    public List<OrganizationResponseDTO> getLedgerOrganizationListAreaTree(){

        return OrganizationBean.getLedgerOrganizationAreaTree();
    }

    @RequestMapping(value = "/getOrganizationGrade1")
    public List<Map> getOrganizationGrade1(){
        return projectsRepository.getOrganizationGrade1();
    }

    @RequestMapping(value = "/getOrganizationGrade2")
    public List<Map> getOrganizationGrade2(){
        return projectsRepository.getOrganizationGrade2();
    }

    @RequestMapping(value = "/getOrganizationGrade3")
    public List<Map> getOrganizationGrade3(){
        return projectsRepository.getOrganizationGrade3();
    }

    @RequestMapping(value = "/getOrganizationGrade4")
    public List<Map> getOrganizationGrade4(){
        return projectsRepository.getOrganizationGrade4();
    }

    @RequestMapping("/gerUserInformation")
    public String getUserDetails(){
        return UserBean.getUserInfo();
    }

    @GetMapping(value = "/loadOrganizationList")
    public ResponseEntity<?> getAllOrganization() {
        List<OrganizationResponseDTO> organizations = projectsService.getOrganizationHierarchy();
        return new ResponseEntity<>(organizations, HttpStatus.OK);
    }

    @PutMapping(value = "/addProjectListDetails")
    public ResponseEntity<?> addNewProjectDetails(@RequestBody Map<String,Object> requestMap){
        Projects projs = new Projects();
        ResultMsg<Projects> response = new ResultMsg<>();
        try{
            int projDetails = projectsService.saveNewProjectDetails(requestMap);
            response.setMessage("Insert success");
            response.setSuccess(true);
            response.setData(projs);
        }catch (Exception e){
            log.error(e.getMessage());
            response.setMessage(e.getLocalizedMessage());
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(projs,HttpStatus.OK);
    }

    @PutMapping(value = "/editProjectListDetails/{id}")
    public ResponseEntity<?> editProjectDetails(@RequestBody Projects projects, @PathVariable("id") String projectId){
        ResultMsg<?> response = new ResultMsg<>();
        try{
           projectsService.editProjectDetails(projects);
           response.setMessage("Edit success");
           response.setSuccess(true);
        }catch(Exception e){
            log.error(e.getMessage());
            response.setMessage(e.getLocalizedMessage());
            response.setSuccess(false);
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @DeleteMapping(value = "/deleteProjectDetails/{id}")
    public ResponseEntity<?> deleteProjectDetails(@PathVariable("id") String projectId){
        ResultMsg<?> response = new ResultMsg<>();
        try{
            projectsService.deleteProjectDetails(projectId);
            response.setMessage("Delete success");
            response.setSuccess(true);
        }catch(Exception e){
            log.error(e.getMessage());
            response.setMessage(e.getLocalizedMessage());
            response.setSuccess(false);
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @SneakyThrows
    @PostMapping("/exportToExcelFlowList")
    public void ExportToExcelFlowList(@RequestBody List<Map<String,Object>> requestMap, HttpServletResponse response){

        List<Map<String, Object>> ledgerItems = requestMap;

        ledgerItems.stream().peek(xx -> {
            if(xx.get("suspend").toString().equals("0")){
                xx.put("suspend", "normal");
            } else {
                xx.put("suspend", "suspend");
            }
        }).collect(Collectors.toList());

        // this class can reuse in other module of export function
        ByteArrayOutputStream stream = ExcelExportIns.getInstance().
                SetParams("docs/Flow List Data Template.xlsx", requestMap)
                .SetColumns(" ","subject", "defName","suspend","status", "createOrgName", "currentTaskApproverNode",
                        "currentTaskApprover", "createByName", "createTimeStr", "duration")
                .build();

        response.setStatus(HttpServletResponse.SC_OK);

        response.setContentType("application/vnd.ms-excel");
        response.setHeader("attachment", "flow_list_data_record.xlsx");

        OutputStream out = response.getOutputStream();
        out.write(stream.toByteArray());
        out.flush();
        out.close();
    }
}
