package com.microservice.systemservice.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultMsg<T> {

    private boolean success;
    private String message;
    private T data;

    public ResultMsg() {
        setSuccess(true);
    }

    public ResultMsg(String message) {
        setSuccess(true);
        this.message = message;
    }

    public ResultMsg(T data) {
        setSuccess(true);
        this.data = data;
    }

    public ResultMsg(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ResultMsg(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }


}
