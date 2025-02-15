package com.esportify.mapper;

import com.esportify.dto.AskDTO;
import com.esportify.entity.Ask;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AskMapper {

    public static AskDTO toDTO(Ask ask) {
        if(Objects.isNull(ask)) return null;
        AskDTO dto = new AskDTO();
        dto.setUuid(ask.getUuid());
        dto.setType(ask.getType());
        if(Objects.isNull(ask.getAuthor()) || ask.getAuthor().isNew()) return null;
        dto.setAuthor(UserMapper.toDTO(ask.getAuthor()));
        dto.setStatus(ask.getStatus());
        dto.setAdminComment(ask.getAdminComment());
        dto.setMessage(ask.getMessage());
        return dto;
    }

    public static List<AskDTO> toDTOList(List<Ask> asks) {
        if(Objects.isNull(asks) || asks.isEmpty()) return Collections.emptyList();
        return asks.stream()
                .map(AskMapper::toDTO)
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }
}
