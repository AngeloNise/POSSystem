package com.microservice.systemservice.services;

import com.microservice.systemservice.dto.ResourcesSetDto;
import com.microservice.systemservice.models.Resources;
import com.microservice.systemservice.repository.SystemResourceRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemResourceService {

    private final SystemResourceRepository systemResourceRepository;

    public Set<ResourcesSetDto> getMenuResourcesSet(Map<String, Object> roleAccount) {
        //Long roleId = 1461L;
        Set<Resources> resourceMenu = systemResourceRepository.findByRealSubResoSet(MapUtils.getLong(roleAccount,"roleId",122L));
        //Set<Resources> resourceMenu = systemResourceRepository.findByName_("QA");
        resourceMenu = orderFillingResourcesSet(resourceMenu);

        //return resourceMenu.stream().map(this::mapResourceSetToResourcesSetDTO).collect(Collectors.toSet());
        return resourceMenu.stream().map(this::mapResourceSetToResourcesSetDTO).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<Resources> orderFillingResourcesSet(Set<Resources> resourceMenu) {
        TreeSet<Resources> topResoSet = new TreeSet<>();
        Set<Resources> childResoSet = new HashSet<>();

        for (Resources orgReso : resourceMenu) {
            if (orgReso.getResourceGrade() == 0) {
                topResoSet.add(orgReso);
            } else {
                childResoSet.add(orgReso);
            }
        }
        for (Resources topReso : topResoSet) {
            resourceAddChild(topReso, childResoSet, 0);
        }
        return topResoSet;
    }

    private void resourceAddChild(Resources topReso, Set<Resources> childResoSet, int count) {
        TreeSet<Resources> resoSet = new TreeSet<>();
        for (Resources orgReso : childResoSet) {
            if (null != orgReso.getParentId()) {
                if (topReso.getResourcesId().equals(orgReso.getParentId())) {
                    resoSet.add(orgReso);
                    count++;
                }
            }
        }
        if (resoSet.size() > 0) {
            topReso.setRealSubResoSet(resoSet);
            for (Resources reso : topReso.getRealSubResoSet()) {
                resourceAddChild(reso, childResoSet, count);
            }
            topReso.setRealSubCount((long) topReso.getRealSubResoSet().size());
        } else topReso.setRealSubCount(0L);
    }

    private ResourcesSetDto mapResourceSetToResourcesSetDTO(Resources resource) {
        return ResourcesSetDto.builder()
                .resourcesId(resource.getResourcesId())
                .parentId(resource.getParentId() != null ? resource.getParentId():0L)
                .realSubResoSet(resource.getRealSubResoSet())
                .realSubCount(resource.getRealSubCount())
                .resourceName(resource.getResourceName())
                .resourceValue(resource.getResourceValue())
                .iconUrl(resource.getIconUrl())
                .build();
    }
}

