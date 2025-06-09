package com.microservice.systemservice.repository;

import com.microservice.systemservice.models.Resources;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

@Mapper
public interface SystemResourceRepository  {
    Set<Resources> findByRealSubResoSet(@Param("roleId") long roleId);
    Set<Resources> findByName_(String name_);
}
