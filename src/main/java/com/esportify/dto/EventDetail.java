package com.esportify.dto;

import com.esportify.enumerations.EventParticipantStatus;

public class EventDetail {

    private EventDTO event;
    private int nbParticipants;
    private EventParticipantStatus eventParticipantStatus;
    private boolean canRegister;
    private boolean canUnregister;

    public EventDTO getEvent() {
        return event;
    }

    public void setEvent(EventDTO event) {
        this.event = event;
    }

    public int getNbParticipants() {
        return nbParticipants;
    }

    public void setNbParticipants(int nbParticipants) {
        this.nbParticipants = nbParticipants;
    }

    public void setEventParticipantStatus(EventParticipantStatus eventParticipantStatus) {
        this.eventParticipantStatus = eventParticipantStatus;
    }

    public EventParticipantStatus getEventParticipantStatus() {
        return eventParticipantStatus;
    }

    public void setCanRegister(boolean canRegister) {
        this.canRegister = canRegister;
    }

    public boolean isCanRegister() {
        return canRegister;
    }

    public void setCanUnregister(boolean canUnregister) {
        this.canUnregister = canUnregister;
    }

    public boolean isCanUnregister() {
        return canUnregister;
    }
}
