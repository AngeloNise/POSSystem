package com.microservice.systemservice.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.systemservice.dto.RedisNotificationPayload;
import com.microservice.systemservice.services.EmitterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
/*import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;*/
import org.springframework.stereotype.Service;

/**
 * @author Angel
 * @created 13/01/2023 - 1:38 PM
 */
/*@Service
@Slf4j
@RequiredArgsConstructor*/
public class RedisMessageSubscriber { //implements MessageListener {

   /*private final EmitterService emitterService;

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onMessage(final Message message, final byte[] pattern) {
        try {
            var notificationPayload = objectMapper.readValue(message.toString(), RedisNotificationPayload.class);

            emitterService.pushNotification(
                    notificationPayload.getUsername(),
                    notificationPayload.getFrom(),
                    notificationPayload.getMessage());

        } catch (JsonProcessingException e) {
            log.error("unable to deserialize message ", e);
        }
    }*/
}
