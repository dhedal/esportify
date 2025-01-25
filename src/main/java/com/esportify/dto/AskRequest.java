package com.esportify.dto;

import com.esportify.enumerations.AskType;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AskRequest {

    @NotNull(message = "le status est obligatoire")
    private AskType type;
    @Size(max = 500, message = "Le message de l'auteur ne doit pas dépasser 500 caractères.")
    private String message;

    public AskType getType() {
        return type;
    }

    public void setType(AskType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
