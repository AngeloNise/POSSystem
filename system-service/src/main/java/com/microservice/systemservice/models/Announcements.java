package com.microservice.systemservice.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.microservice.systemservice.helper.LocalDateDeserializer;
import com.microservice.systemservice.helper.LocalDateSerializer;
import lombok.*;
import org.springframework.data.annotation.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

/**
 * @author Angel
 * @created 27/10/2022 - 10:33 AM
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Announcements implements Serializable {

    @Id
    private int announcementId;
    private String subject;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate publishedDate;
    private String content;
    //private String userTo;
    //private String userFrom;
    private int thread;
    //private boolean delivered;
    //private boolean read; //should be stored in redis then forward to SSE Emitter
    // boolean isBroadcast; //redis value
    //private String type; //Change to enum for future announcement/notification types
    @Transient
    private String escapedContent; //escape html tag on messages/content
}
