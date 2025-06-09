package com.microservice.systemservice.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResultMsg2<T> {
    private boolean success;
    private String message;
    private T data;

    public ResultMsg2(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
