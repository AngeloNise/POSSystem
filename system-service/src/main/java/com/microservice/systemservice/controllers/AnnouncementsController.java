package com.microservice.systemservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.microservice.systemservice.configs.RedisConfig;
import com.microservice.systemservice.models.Announcements;
import com.microservice.systemservice.models.PageResult;
import com.microservice.systemservice.models.ResultMsg;
import com.microservice.systemservice.models.TaskNotif;
import com.microservice.systemservice.repository.AnnouncementsRepository;
import com.microservice.systemservice.services.AnnouncementsService;
import com.microservice.systemservice.services.EmitterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Range;
/*import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;*/
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Angel
 * @created 26/10/2022 - 4:53 PM
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/announcements")
@Slf4j
public class AnnouncementsController {
    private final AnnouncementsService announcementsService;
    private final AnnouncementsRepository announcementsRepository;
    //private final RedisTemplate redisTemplate;
    //private final RedisMessageListenerContainer messageListenerContainer;
    private final EmitterService emitterService;
    public List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    public static final String NOTIFICATIONS_CHANNEL = "notifications:";

    @PostMapping(value = "/get-announcements")
    public List<Announcements> getAnnouncements(@RequestBody Map<String,Object> requestMap){
        return announcementsService.getAllAnnouncementsParam(requestMap);
    }
    @RequestMapping(value = "/subscribe", consumes = MediaType.ALL_VALUE)
    public SseEmitter subscribe() {
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        try{
            sseEmitter.send(SseEmitter.event().name("INIT"));
        }catch (IOException e){
            //log.error(e.getLocalizedMessage());
            emitters.remove(sseEmitter);
        }
        emitters.add(sseEmitter);
        sseEmitter.onCompletion(() -> emitters.remove(sseEmitter));
        sseEmitter.onError(e -> emitters.remove(sseEmitter));
        sseEmitter.onTimeout(()-> emitters.remove(sseEmitter));
        dispatchNotifications();
        return sseEmitter;
    }

    /*@GetMapping("/notifications/{name}")
    public SseEmitter getNotifications(@PathVariable String name) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        //emitterService.addEmitter(emitter);
        MessageListener listener = (message, pattern) -> {
            Announcements announcements = serialize(message);
            try {
                emitter.send(SseEmitter.event().data(announcements).id(String.valueOf(announcements.getAnnouncementId()))
                        .name("notification"));
            }
            catch (IOException ex) {
                emitter.completeWithError(ex);
                emitters.remove(emitter);
            }
        };
        //messageListenerContainer.addMessageListener(listener, ChannelTopic.of(NOTIFICATIONS_CHANNEL+name));
        emitter.onCompletion(() -> {
            emitters.remove(emitter);
            //messageListenerContainer.removeMessageListener(listener);
        });
        return emitter;
    }*/

    /*private Announcements serialize(Message message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return objectMapper.readValue(message.getBody(), Announcements.class);
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }*/

    @RequestMapping(value = "/dispatch")
    public void dispatchNotifications(){
        for(SseEmitter sseEmitter: emitters){
            try{
                sseEmitter.send(SseEmitter.event().name("notification-list-event").data(getAnnouncements(null)));
            }catch (IOException e){
                emitters.remove(sseEmitter);
            }
        }
    }

    @PostMapping(value = "/post-announcement")
    public ResponseEntity<?> postAnnouncement(@ModelAttribute Announcements announcements){
        ResultMsg response = new ResultMsg();
        try{
            announcementsService.saveAnnouncement(announcements);
            response.setSuccess(true);
            response.setMessage("Successfully inserted announcement");
            dispatchNotifications();
        }catch(Exception e ){
            log.error(e.getMessage());
            response.setSuccess(false);
            response.setMessage(e.getLocalizedMessage());
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping(value="/delete-announcement/{id}")
    public ResponseEntity<?> deleteAnnouncement(@PathVariable("id") Long announcementId){
        ResultMsg response = new ResultMsg();
        try{
            announcementsService.deleteAnnouncement(announcementId);
            response.setSuccess(true);
            response.setMessage("Successfully deleted announcement");
            dispatchNotifications();
        }catch (Exception e){
            log.error(e.getMessage());
            response.setSuccess(false);
            response.setMessage(e.getLocalizedMessage());
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(value = "/post-announcement/{userId}")
    public ResponseEntity<?> postAnnouncementToSpecificUser(@PathVariable("userId") String username,
                                                            @ModelAttribute Announcements announcements){
        ResultMsg response = new ResultMsg();
        try{
            //long now = Instant.now().toEpochMilli();
            //announcementsService.saveAnnouncement(announcements);
            announcementsService.addAnnouncementToRedisShard(username,announcements);
            response.setSuccess(true);
            response.setMessage("Successfully inserted announcement");
           /* redisTemplate.opsForZSet().addIfAbsent(NOTIFICATIONS_CHANNEL+username,
                    announcements,now);
            redisTemplate.convertAndSend(NOTIFICATIONS_CHANNEL+username,announcements);*/
            //dispatchNotifications();
        }catch(Exception e ){
            log.error(e.getMessage());
            response.setSuccess(false);
            response.setMessage(e.getLocalizedMessage());
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @GetMapping(value = "/getAllAnouncementsOfUser/{userId}")
    public ResponseEntity<?> getAllAnouncementsOfUser(@PathVariable("userId") String username){
        ResultMsg response = new ResultMsg();
        try {
          /* Set<Object> allAnnouncements = redisTemplate.opsForZSet().rangeByScore(
                   NOTIFICATIONS_CHANNEL+username,Instant.ofEpochMilli(Long.MIN_VALUE).toEpochMilli(),Instant.ofEpochMilli(Long.MAX_VALUE).toEpochMilli());

            response.setData(allAnnouncements);*/
            response.setSuccess(true);
        }catch(Exception e){
            log.error(e.getMessage());
            response.setSuccess(false);
            response.setMessage(e.getLocalizedMessage());
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping(value = "/getRecentAnnouncementsOfUser/{userId}")
    public ResponseEntity<ResultMsg> getRecentAnouncementsOfUser(@PathVariable("userId") String username){
        ResultMsg response = new ResultMsg();
        try {
            Instant now = Instant.now();
            /*
            Set<Announcements> recentAnnouncement = redisTemplate.opsForZSet().rangeByScore(
                    StringUtils.join(NOTIFICATIONS_CHANNEL,username),
                    now.minus(6, ChronoUnit.HALF_DAYS).toEpochMilli(),
                    now.toEpochMilli());


            response.setData(recentAnnouncement);*/
            response.setSuccess(true);
        }catch(Exception e){
            log.error(e.getMessage());
            response.setSuccess(false);
            response.setMessage(e.getLocalizedMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping(value = "getTaskNotifications")
    public PageResult<TaskNotif> getTaskNotifs(@RequestParam("userId") String userId) {
        return announcementsService.getTaskNotifs(userId);
    }

}
