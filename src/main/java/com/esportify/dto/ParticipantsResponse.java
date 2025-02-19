package com.esportify.dto;

import com.esportify.entity.EventParticipant;

import java.util.ArrayList;
import java.util.List;

public class ParticipantsResponse extends Response{

    private EventDTO event;
    private List<ParticipantDTO> participants = new ArrayList<>();

    public EventDTO getEvent() {
        return event;
    }

    public void setEvent(EventDTO event) {
        this.event = event;
    }

    public void setParticipants(List<ParticipantDTO> participants) {
        this.participants = participants;
    }

    public List<ParticipantDTO> getParticipants() {
        return participants;
    }
}
