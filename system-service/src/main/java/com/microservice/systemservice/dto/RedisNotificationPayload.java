package com.microservice.systemservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Angel
 * @created 13/01/2023 - 1:40 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisNotificationPayload {
    private String username;
    private String from;
    private String message;
}
