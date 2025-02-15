package com.esportify.dto;

import com.esportify.enumerations.AskStatus;
import com.esportify.enumerations.AskType;

public class AskDTO {
    private String uuid;
    private AskType type;
    private UserDTO author;
    private String adminComment;
    private AskStatus status;
    private String message;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public AskType getType() {
        return type;
    }

    public void setType(AskType type) {
        this.type = type;
    }

    public UserDTO getAuthor() {
        return author;
    }

    public void setAuthor(UserDTO author) {
        this.author = author;
    }

    public String getAdminComment() {
        return adminComment;
    }

    public void setAdminComment(String adminComment) {
        this.adminComment = adminComment;
    }

    public AskStatus getStatus() {
        return status;
    }

    public void setStatus(AskStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
