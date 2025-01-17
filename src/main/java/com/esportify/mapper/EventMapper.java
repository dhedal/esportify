package com.esportify.mapper;

import com.esportify.dto.EventDTO;
import com.esportify.entity.Event;

import java.util.Objects;

public class EventMapper {

    public static EventDTO toDTO(Event event) {
        if(Objects.isNull(event)) throw new IllegalArgumentException("event ne doit pas être null");
        EventDTO dto = new EventDTO();
        dto.setUuid(event.getUuid());
        dto.setDescription(event.getDescription());
        dto.setMaxPlayers(event.getMaxPlayers());
        dto.setStatus(event.getStatus());
        dto.setStartDateTime(event.getStartDateTime());
        dto.setEndDateTime(event.getEndDateTime());
        dto.setOrganizer(UserMapper.toDTO(event.getOrganizer()));
        return dto;
    }
}
