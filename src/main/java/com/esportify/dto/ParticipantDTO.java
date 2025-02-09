package com.esportify.dto;

import com.esportify.enumerations.EventParticipantStatus;

public class ParticipantDTO {
    private String eventUuid;
    private EventParticipantStatus status;
    private UserDTO participant;

    public String getEventUuid() {
        return eventUuid;
    }

    public void setEventUuid(String eventUuid) {
        this.eventUuid = eventUuid;
    }

    public EventParticipantStatus getStatus() {
        return status;
    }

    public void setStatus(EventParticipantStatus status) {
        this.status = status;
    }

    public UserDTO getParticipant() {
        return participant;
    }

    public void setParticipant(UserDTO participant) {
        this.participant = participant;
    }
}
