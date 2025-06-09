package com.microservice.systemservice.dto;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;
/**
 * @author Angel
 * @created 03/11/2022 - 2:52 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonPropertyOrder({"httpStatusCode", "message", "data", "otherParams" })
public class ApiResponse<T> {

    private int httpStatusCode;
    private String message;
    private T data;
    private Map<String, Object> otherParams;
}