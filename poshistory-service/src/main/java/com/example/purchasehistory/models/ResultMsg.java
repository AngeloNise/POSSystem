package com.example.purchasehistory.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultMsg<T> {

    private T data;
    private String message;
    private boolean success;

    public ResultMsg<T> success(T data) {
        this.data = data;
        this.success = true;
        this.message = "Operation successful";
        return this;
    }

    public ResultMsg<T> failure(String message) {
        this.success = false;
        this.message = message;
        return this;
    }
}
