package com.example.possupplierservice.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultMsg<T> {

    private T data;
    private String message;
    private boolean success;

    // Constructor for initializing success, message, and data
    public ResultMsg(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Default constructor
    public ResultMsg() {
        this.success = true; // Default success state
    }

    // Success method with message
    public ResultMsg<T> success(T data) {
        this.data = data;
        this.success = true;
        this.message = "Operation successful";
        return this;
    }

    // Success method with custom message
    public ResultMsg<T> success(T data, String message) {
        this.data = data;
        this.success = true;
        this.message = message;
        return this;
    }

    // Failure method
    public ResultMsg<T> failure(String message) {
        this.success = false;
        this.message = message;
        return this;
    }
}
