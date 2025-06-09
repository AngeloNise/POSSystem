package com.gateway.edge.service.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Default on 25/04/2022.
 */

public class Account {

    private long id_;
    private String name_;
    private String username_;
    private String password_;
    private String email_;


    public long getId_() {
        return id_;
    }

    public void setId_(long id_) {
        this.id_ = id_;
    }

    public String getName_() {
        return name_;
    }

    public void setName_(String name_) {
        this.name_ = name_;
    }

    public String getUsername_() {
        return username_;
    }

    public void setUsername_(String username_) {
        this.username_ = username_;
    }

    public String getPassword_() {
        return password_;
    }

    public void setPassword_(String password_) {
        this.password_ = password_;
    }

    public String getEmail_() {
        return email_;
    }

    public void setEmail_(String email_) {
        this.email_ = email_;
    }
}
