package com.esportify.dto;

import com.esportify.enumerations.EventStatus;
import com.esportify.validation.ValidUUID;

import javax.validation.constraints.NotBlank;

public class EventStatusRequest {

    @NotBlank(message = "L'uuid est obligatoire")
    @ValidUUID(message = "L'uuid doit être valide")
    private String uuid;
    private EventStatus status;
}
