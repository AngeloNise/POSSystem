package com.microservice.systemservice.dto;

import lombok.*;

/**
 * @author Angel
 * @created 15/02/2023 - 3:41 PM
 */
@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RedisKeyDto {
    String key;
    Object value;
    String type;
}
