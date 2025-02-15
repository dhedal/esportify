package com.esportify.dto;

public class ChangeAskStatusRequest extends Response{

    private String uuid;
    private Integer statusKey;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getStatusKey() {
        return statusKey;
    }

    public void setStatusKey(Integer statusKey) {
        this.statusKey = statusKey;
    }
}
