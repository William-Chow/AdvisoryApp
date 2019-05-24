package com.william.advisory.mvp.model.entity;

/**
 * Created by William Chow on 2019-05-23.
 */
public class UserProfile {
    private int id;
    private String token;
    private Status status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
