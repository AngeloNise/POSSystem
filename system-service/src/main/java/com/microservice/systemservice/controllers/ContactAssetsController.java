package com.microservice.systemservice.controllers;


import com.microservice.systemservice.models.ContactAssets;
import com.microservice.systemservice.models.ResultMsg;
import com.microservice.systemservice.services.ContactAssetsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contactassets")
@Slf4j
public class ContactAssetsController {

    private final ContactAssetsService contactAssetsService;

    @GetMapping(value = "/loadContactList")
    public List<ContactAssets> getAllContactAssetList() {
       // Map<Object, List<ContactAssets>> allUsers = contactAssetsService.getAllContactAssetList();
        List<ContactAssets> allUsers = contactAssetsService.getAllContactAssetList();
        return allUsers;
    }

    @PutMapping(value = "/updateContactAsset/{id}")
    public ResponseEntity<?> updateUserDataDetails(@PathVariable("id") long id, @RequestBody ContactAssets contactAssets){
        ContactAssets returnValue = new ContactAssets();

        contactAssetsService.updateContactAsset(id, contactAssets);

        return new ResponseEntity<>("Successfully updated!", HttpStatus.OK);
    }

    @PostMapping(value = "/updateContactListProject/{id},{newValue}")
    public ResponseEntity<?> updateContactListProject(@PathVariable("id") Long id, @PathVariable("newValue") String newProjectName){

        ContactAssets contactAssets = contactAssetsService.updateContactListProject(id, newProjectName);

        return new ResponseEntity<>(contactAssets,HttpStatus.OK);
    }

    @PutMapping(value="/saveContactListData")
    public ResponseEntity<?> saveNewAssetContact(@RequestBody ContactAssets contactAsset){
        ResultMsg response = new ResultMsg();
        try{
            contactAssetsService.saveContactList(contactAsset);
            response.setSuccess(true);
            response.setMessage("Successfully added contact list");
        }catch (Exception e){
            log.error(e.getMessage());
            response.setSuccess(false);
            response.setMessage(e.getLocalizedMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/deleteContactAssetInfo/{id}")
    public ResponseEntity<?> deleteContactAssetInfo(@PathVariable("id") Long id){
        ResultMsg response = new ResultMsg();
        try{
            contactAssetsService.deleteContactAssetInfo(id);
            response.setSuccess(true);
            response.setMessage("Successfully deleted contact list");
        }catch (Exception e){
            log.error(e.getMessage());
            response.setSuccess(false);
            response.setMessage(e.getLocalizedMessage());
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(value = "/modifyProjectName")
    public ResponseEntity<?> modifyNewProjectName(@RequestParam Map<String,Object> requestMap){
        ResultMsg response = new ResultMsg();
        try{
            contactAssetsService.modifyNewProjectName(requestMap);
            response.setSuccess(true);
            response.setMessage("Successfully deleted contact list");
        }catch (Exception e){
            log.error(e.getMessage());
            response.setSuccess(false);
            response.setMessage(e.getLocalizedMessage());
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

}
