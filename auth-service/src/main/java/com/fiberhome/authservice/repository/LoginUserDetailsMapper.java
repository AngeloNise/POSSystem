package com.fiberhome.authservice.repository;

import com.fiberhome.authservice.model.Account;
import com.fiberhome.authservice.model.ActivityLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mapper
public interface LoginUserDetailsMapper {
    Optional<Account> findByUsername(@Param("userName") String userName);

    Map getUserByID(Long id);

    List<Map> GetAllOrganizationList();

    void insertActivityLog2(ActivityLog activityLog);
}
