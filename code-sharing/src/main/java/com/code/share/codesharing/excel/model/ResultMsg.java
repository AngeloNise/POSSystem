package com.code.share.codesharing.excel.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResultMsg<T> {
    private boolean success;
    private String message;
    private T data;

    public ResultMsg(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
