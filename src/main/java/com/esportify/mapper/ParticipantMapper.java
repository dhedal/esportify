package com.esportify.mapper;

import com.esportify.dto.EventParticipantDTO;
import com.esportify.dto.ParticipantDTO;
import com.esportify.entity.EventParticipant;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ParticipantMapper {

    public static ParticipantDTO toDTO(EventParticipant eventParticipant) {
        if(Objects.isNull(eventParticipant)) return null;
        ParticipantDTO dto = new ParticipantDTO();
        if(!Objects.isNull(eventParticipant.getEvent())) {
            dto.setEventUuid(eventParticipant.getEvent().getUuid());
        }
        dto.setParticipant(UserMapper.toDTO(eventParticipant.getParticipant()));
        dto.setStatus(eventParticipant.getStatus());
        return dto;
    }

    public static List<ParticipantDTO> toDTOList(List<EventParticipant> eventParticipants) {
        if(Objects.isNull(eventParticipants) || eventParticipants.isEmpty()) return Collections.emptyList();
        return eventParticipants.stream()
                .map(ParticipantMapper::toDTO)
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }
}
