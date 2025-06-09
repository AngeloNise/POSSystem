package com.microservice.systemservice.controllers;

import com.microservice.systemservice.dto.ResourcesSetDto;
import com.microservice.systemservice.services.SystemResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resource")
public class ResourcesController {
    private final SystemResourceService systemResourceService;

    @PostMapping(value = "/resMenu")
    @ResponseStatus(HttpStatus.OK)
    public Set<ResourcesSetDto> getMenuResourceSet(@RequestBody Map<String, Object> roleAccount) {
        Set<ResourcesSetDto> check = systemResourceService.getMenuResourcesSet(roleAccount);
        return check;
    }

}
