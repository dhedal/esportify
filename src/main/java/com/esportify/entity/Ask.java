package com.esportify.entity;

import com.esportify.enumerations.AskStatus;
import com.esportify.enumerations.AskType;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
public class Ask extends AbstractEntity implements Serializable {

    @Column(nullable = false)
    private AskType type;
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;
    @Column(nullable = true, length = 500)
    private String message;
    @Column(nullable = false)
    private AskStatus status;
    @Column(nullable = true, length = 500)
    private String adminComment;

    public AskType getType() {
        return type;
    }

    public void setType(AskType type) {
        this.type = type;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AskStatus getStatus() {
        return status;
    }

    public void setStatus(AskStatus status) {
        this.status = status;
    }

    public String getAdminComment() {
        return adminComment;
    }

    public void setAdminComment(String adminComment) {
        this.adminComment = adminComment;
    }

    public boolean equals(Object o) {
        return super.equals(o);
    }

    public int hashCode() {
        return super.hashCode();
    }
}
