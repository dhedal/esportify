package com.esportify.dto;

import com.esportify.enumerations.AskStatus;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ProcessAskRequest extends UUIDRequest{
    @NotNull(message = "le status est obligatoire")
    private AskStatus status;
    @Size(max = 500, message = "Le commentaire de l'administrateur ne doit pas dépasser 500 caractères.")
    private String adminComment;
    public ProcessAskRequest() { super();}

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
}
