package com.code.share.codesharing.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResultMsg<T> {
    private boolean success;

    private String severity;
    private String message;
    private T data;

    public ResultMsg<T> success() {
        this.success = true;
        return this;
    }

    public ResultMsg<T> success(String message) {
        this.success = true;
        this.message = message;
        return this;
    }

    public ResultMsg<T> success(T data) {
        this.success = true;
        this.data = data;
        return this;
    }

    public ResultMsg<T> success(T data, String message) {
        this.success = true;
        this.data = data;
        this.message = message;
        return this;
    }

    public ResultMsg<T> failure() {
        this.success = false;
        return this;
    }

    public ResultMsg<T> failure(T data, String message) {
        this.success = false;
        this.data = data;
        this.message = message;
        return this;
    }
    public ResultMsg<T> failure(String message) {
        this.success = false;
        this.message = message;
        return this;
    }

    public ResultMsg<T> failure(String message, String severity) {
        this.severity = severity;
        this.success = false;
        this.message = message;
        return this;
    }
    public ResultMsg<T> success(String message, String severity) {
        this.severity = severity;
        this.success = true;
        this.message = message;
        return this;
    }



}
