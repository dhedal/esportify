package com.esportify.dto;

import com.esportify.entity.EventParticipant;

import java.util.ArrayList;
import java.util.List;

public class ParticipantsResponse extends Response{

    private List<ParticipantDTO> participants = new ArrayList<>();

    public void setParticipants(List<ParticipantDTO> participants) {
        this.participants = participants;
    }

    public List<ParticipantDTO> getParticipants() {
        return participants;
    }
}
