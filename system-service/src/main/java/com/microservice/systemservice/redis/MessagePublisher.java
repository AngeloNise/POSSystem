package com.microservice.systemservice.redis;

import com.microservice.systemservice.dto.RedisNotificationPayload;

/**
 * @author Angel
 * @created 13/01/2023 - 1:06 PM
 */
public interface MessagePublisher {
    void publish(final RedisNotificationPayload message);
}