package com.microservice.systemservice.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * @author Angel
 * @created 26/10/2022 - 1:54 PM
 */
@Data
@Builder
public class MessageResponse {

    private Long announcementId;
    private String publishedDate;
    private String postedBy;
    private String messageContent;
}
