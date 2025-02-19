package com.esportify.dto;

import com.esportify.validation.ValidUUID;
import jakarta.validation.constraints.NotBlank;

;

public class ParticipantRejectRequest {
    @NotBlank(message = "L'event uuid est obligatoire")
    @ValidUUID(message = "L'event uuid doit être valide")
    private String eventUuid;

    @NotBlank(message = "Le participant uuid est obligatoire")
    @ValidUUID(message = "Le participant uuid doit être valide")
    private String participantUuid;

    public String getEventUuid() {
        return eventUuid;
    }

    public void setEventUuid(String eventUuid) {
        this.eventUuid = eventUuid;
    }

    public String getParticipantUuid() {
        return participantUuid;
    }

    public void setParticipantUuid(String participantUuid) {
        this.participantUuid = participantUuid;
    }
}
