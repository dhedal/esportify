package com.esportify.dto;

import com.esportify.validation.ValidUUID;

import javax.validation.constraints.NotBlank;

public class UpdateEventRequest extends EventRequest{
    @NotBlank(message = "L'uuid est obligatoire")
    @ValidUUID(message = "L'uuid doit être valide")
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
