package com.esportify.mapper;

import com.esportify.dto.EventParticipantDTO;
import com.esportify.entity.EventParticipant;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EventParticipantMapper {

    public static EventParticipantDTO toDTO(EventParticipant eventParticipant) {
        if(Objects.isNull(eventParticipant)) return null;
        EventParticipantDTO dto = new EventParticipantDTO();
        dto.setEvent(EventMapper.toDTO(eventParticipant.getEvent()));
        dto.setParticipant(UserMapper.toDTO(eventParticipant.getParticipant()));
        dto.setStatus(eventParticipant.getStatus());
        dto.setScore(eventParticipant.getScore() == null ?
                0 : eventParticipant.getScore().intValue());
        return dto;
    }

    public static List<EventParticipantDTO> toDTOList(List<EventParticipant> eventParticipants) {
        if(Objects.isNull(eventParticipants) || eventParticipants.isEmpty()) return Collections.emptyList();
        return eventParticipants.stream()
                .map(EventParticipantMapper::toDTO)
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }
}
