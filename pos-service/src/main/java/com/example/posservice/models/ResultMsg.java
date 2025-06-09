package com.example.posservice.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultMsg<T> {

    private T data;
    private String message;
    private boolean success;

    // Success response constructor
    public ResultMsg<T> success(T data) {
        this.data = data;
        this.success = true;
        this.message = "Operation successful";
        return this;
    }

    // Failure response constructor
    public ResultMsg<T> failure(String message) {
        this.success = false;
        this.message = message;
        return this;
    }
}
