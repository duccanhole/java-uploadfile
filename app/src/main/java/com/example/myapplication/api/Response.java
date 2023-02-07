package com.example.myapplication.api;

import com.google.gson.annotations.SerializedName;

public class Response {
    @SerializedName("code")
    private int code;
    @SerializedName("status")
    private String status;
    @SerializedName("results._id")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }
}
