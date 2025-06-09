package com.microservice.systemservice.services;

import com.microservice.systemservice.models.Announcements;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Angel
 * @created 13/01/2023 - 2:12 PM
 */
@Service
@Slf4j
public class EmitterService {

    //List<SseEmitter> emitters = new ArrayList<>();
    List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    public void addEmitter(SseEmitter emitter) {
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitters.add(emitter);
    }

    public void pushNotification(String username, String name, String message) {
        log.info("pushing {} notification for user {}", message, username);
        List<SseEmitter> deadEmitters = new ArrayList<>();

        //Announcements
        Announcements payload = Announcements
                .builder()
                //.userFrom(name)
                .content(message)
                .build();

        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter
                        .event()
                        .name(username)
                        .data(payload)
                );
                emitter.complete();
            } catch (IOException e) {
                emitter.completeWithError(e);
                deadEmitters.add(emitter);
            }
        });

        emitters.removeAll(deadEmitters);
    }
}
