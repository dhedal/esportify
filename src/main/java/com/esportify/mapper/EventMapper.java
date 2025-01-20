package com.esportify.mapper;

import com.esportify.dto.EventDTO;
import com.esportify.entity.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EventMapper {

    public static EventDTO toDTO(Event event) {
        if(Objects.isNull(event)) return null;
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

    public static List<EventDTO> toDTOList(List<Event> events) {
        if(Objects.isNull(events) || events.isEmpty()) return Collections.emptyList();
        List<EventDTO> dtos = new ArrayList<>();
        return events.stream()
                .map(EventMapper::toDTO)
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }
}
