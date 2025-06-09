package com.microservice.systemservice.repository;

import com.microservice.systemservice.models.ContactAssets;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface ContactAssetsRepository {

    int updateContactAssetList(@Param("id") long id, ContactAssets contactAssets);

    List<ContactAssets> loadContactAssetList();

    int saveContactAssetList(ContactAssets contactAsset);

    ContactAssets updateContactListProject(@Param("id") Long id, @Param("projName") String newProjectName);

    int deleteSelectedContactAssetList(@Param("id") Long id);

    int updateContactListProject(Map<String, Object> requestMap);
}
