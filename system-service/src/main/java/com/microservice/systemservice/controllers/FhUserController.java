package com.microservice.systemservice.controllers;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.microservice.systemservice.dto.FhUserResponse;
import com.microservice.systemservice.dto.ProjectsResponse;
import com.microservice.systemservice.dto.RolesResponse;
import com.microservice.systemservice.models.FhUser;
import com.microservice.systemservice.models.Roles;
import com.microservice.systemservice.services.FhUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fhuser")
@Slf4j
public class FhUserController {

    private final FhUserService fhUserService;

    @GetMapping(value = "/loadAllUsers")
    public List<FhUser> getAllFhUsers() {
        List<FhUser> allUsers = fhUserService.getAllFhUsers();
        return allUsers;
    }

    @GetMapping(value = "/loadUserAccess/{fhId}")
    public List<RolesResponse> getUserAccess(@PathVariable("fhId") String fhId){
        List<RolesResponse> fhUserAccess = fhUserService.loadUserAccess(fhId);

        return fhUserAccess;
    }

    @GetMapping(value = "/searchUFhiduser/{search}")
    public List<FhUser> searchFhUsers(@PathVariable("search") String search){
        //TODO: Current LoggedIn User conditioning for access and org/project
        List<FhUser> userResult = fhUserService.searchFhUsers(search);

        return userResult;
    }

    @PutMapping(value = "/updateUserData/{id}")
    public ResponseEntity<?> updateUserDataDetails(@PathVariable("id") String id, @RequestBody FhUser fhUser){
        FhUserResponse returnValue = new FhUserResponse();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
        FhUser userProfileDto= mapper.convertValue(fhUser, FhUser.class);

        returnValue = fhUserService.updateUserData(id, userProfileDto);

        return new ResponseEntity<FhUserResponse>(returnValue, HttpStatus.OK);
    }

    @PutMapping(value = "/insertUserAccess/{id}")
    public ResponseEntity<?> insertUserAccess(@PathVariable("id") String id, @ModelAttribute Roles fhUser){
        RolesResponse returnValue = new RolesResponse();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
        Roles userProfileDto= mapper.convertValue(fhUser, Roles.class);

        returnValue = fhUserService.insertUserAccess(id, userProfileDto);

        return new ResponseEntity<RolesResponse>(returnValue, HttpStatus.OK);
    }

    @PutMapping(value = "/updateUserAccess/{id}")
    public ResponseEntity<?> updateUserAccess(@PathVariable("id") String id, @RequestBody Roles fhUser){
        RolesResponse returnValue = new RolesResponse();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
        Roles userProfileDto= mapper.convertValue(fhUser, Roles.class);

        returnValue = fhUserService.updateUserAccess(id, userProfileDto);

        return new ResponseEntity<RolesResponse>(returnValue, HttpStatus.OK);
    }

    @PutMapping(value="/SaveUserInput")
    public ResponseEntity<?> saveNewFhUser(@RequestBody FhUser fhUser){
        FhUserResponse returnValue = null;
        try{
            //FhUser userProfileDto = new FhUser();
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
            FhUser userProfileDto= mapper.convertValue(fhUser, FhUser.class);
            BeanUtils.copyProperties(fhUser, userProfileDto);

            int storedUserDetails = fhUserService.saveUserInput(userProfileDto);
            returnValue =  new FhUserResponse();
            userProfileDto.setRolesList(List.of(
                    Roles.builder()
                            .fhId(userProfileDto.getFhId())
                            .area(StringUtils.defaultIfBlank(userProfileDto.getArea(),""))
                            .region(StringUtils.defaultIfBlank(userProfileDto.getRegion(),""))
                            .userAccess(StringUtils.defaultIfBlank(userProfileDto.getUserAccess(),""))
                            .permission("ADMIN")
                            .privileges("SET")
                    .build()));
            int ro = fhUserService.saveUserRole(userProfileDto);
            BeanUtils.copyProperties(userProfileDto, returnValue);
            returnValue.setMessage("Successfully created the user!");
            returnValue.setSuccess(true);
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(returnValue, HttpStatus.OK);
    }

    @DeleteMapping(value = "/dumpUser/{id}")
    public ResponseEntity<?> dumpFhUser(@PathVariable("id") String id){
        FhUserResponse returnValue = new FhUserResponse();
        fhUserService.deleteFhUserById(id);
        returnValue.setMessage("Successfully deleted the user!");
        returnValue.setSuccess(true);
        return new ResponseEntity<>(returnValue, HttpStatus.OK);
    }

    @DeleteMapping(value = "/deleteUserAccess/{id}")
    public ResponseEntity<?> deleteUserAccess(@PathVariable("id") Long id){

        fhUserService.deleteUserAccessById(id);

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping(value = "/loadUserProjectSelection")
    public ResponseEntity<?> loadUserProjects(){

        List<ProjectsResponse> projectsResponse = fhUserService.loadUserProjects();

        return ResponseEntity.ok(projectsResponse);
    }
}
