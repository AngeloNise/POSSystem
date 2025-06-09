package com.microservice.systemservice.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;


/**
 * @author Angel
 * @created 03/11/2022 - 2:19 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    private String id;
    private String username;
    private String name;
}
