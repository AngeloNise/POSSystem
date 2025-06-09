package com.microservice.systemservice.repository;

import com.microservice.systemservice.models.Announcements;
import com.microservice.systemservice.models.TaskNotif;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * @author Angel
 * @created 27/10/2022 - 10:29 AM
 */
@Mapper
@Repository
public interface AnnouncementsRepository {
    int insertAnnouncement(Announcements announcements);

    int deleteAnnouncement(@Param("announcementId") Long announcementId);

    List<Announcements> getAllAnnouncements();

    List<Announcements> getAllAnnouncementsParam(Map<String, Object> requestMap);

    @Select("SELECT * FROM announcements WHERE AnnouncementId = #{announcementId}")
    Announcements getSpecificAnnouncementById(@Param("announcementId") String announcementId);

    int getLastAnnouncementId();

    List<TaskNotif> getTaskNotifs(String userId);
    int getTaskNotifsCount(String userId);
}
