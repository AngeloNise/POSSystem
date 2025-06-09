package com.microservice.systemservice.services;

import com.microservice.systemservice.models.Announcements;
import com.microservice.systemservice.models.PageResult;
import com.microservice.systemservice.models.TaskNotif;
import com.microservice.systemservice.repository.AnnouncementsRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
//import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.microservice.systemservice.controllers.AnnouncementsController.NOTIFICATIONS_CHANNEL;

/**
 * @author Angel
 * @created 27/10/2022 - 10:26 AM
 */
@Service
@RequiredArgsConstructor
public class AnnouncementsService {

    private final AnnouncementsRepository announcementsRepository;
    //private final RedisTemplate redisTemplate;
    public void saveAnnouncement(Announcements announcements) {
        int insertedAnnouncementId = announcementsRepository.insertAnnouncement(announcements);
    }

    public void deleteAnnouncement(Long announcementId) {
        int deleted = announcementsRepository.deleteAnnouncement(announcementId);
    }

    private List<Announcements> getAllAnnouncements() {
        return announcementsRepository.getAllAnnouncements();
    }

    public List<Announcements> getAllAnnouncementsParam(Map<String, Object> requestMap) {
        List<Announcements> announcementsList = announcementsRepository.getAllAnnouncementsParam(requestMap);
        announcementsList.sort(Comparator.comparing(Announcements::getPublishedDate));
        return announcementsList.stream().map(announcements ->
        {
            announcements.setEscapedContent(RegExUtils.replaceAll(announcements.getContent(),"<.*?>","\n"));
            return announcements;
        }).collect(Collectors.toList());
    }

    public Announcements getSpecificAnnouncementById(int announcementId){
        return announcementsRepository.getSpecificAnnouncementById(String.valueOf(announcementId));
    }

    public void addAnnouncementToRedisShard(String username,Announcements announcements){
        int lastAnnouncementId = announcementsRepository.getLastAnnouncementId();
       // OptionalLong keySize = OptionalLong.of(redisTemplate.opsForZSet().size(NOTIFICATIONS_CHANNEL+username));
       // int lastAnnouncementKey = keySize.orElse(0L) >= 1? (int) keySize.getAsLong() : lastAnnouncementId;
        //announcements.setAnnouncementId(++lastAnnouncementKey);
        announcements.setPublishedDate(LocalDate.now());
        /* redisTemplate.opsForZSet().addIfAbsent(NOTIFICATIONS_CHANNEL+username,
                announcements, Instant.now().toEpochMilli());
        redisTemplate.convertAndSend(NOTIFICATIONS_CHANNEL+username,announcements); */
    }

    public PageResult<TaskNotif> getTaskNotifs(String userId) {
        int notifLimit = 5;
        List<TaskNotif> notifs = announcementsRepository.getTaskNotifs(userId);
        if (CollectionUtils.isNotEmpty(notifs)) {
            notifs.forEach(TaskNotif::formatCreateTime);
        }
        int count = notifs.size() < notifLimit ? notifs.size() : announcementsRepository.getTaskNotifsCount(userId);
        return new PageResult<>(notifs, count, notifLimit);
    }

}
