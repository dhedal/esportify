package com.esportify.dto;

import com.esportify.enumerations.EventParticipantStatus;

public class EventParticipantDTO {

    private EventDTO event;
    private EventParticipantStatus status;
    private UserDTO participant;
    private int score;

    public EventDTO getEvent() {
        return event;
    }

    public void setEvent(EventDTO event) {
        this.event = event;
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
