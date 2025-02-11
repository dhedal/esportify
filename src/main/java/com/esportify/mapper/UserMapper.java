package com.esportify.mapper;

import com.esportify.dto.UserDTO;
import com.esportify.entity.User;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserDTO toDTO(User user) {
        if(Objects.isNull(user)) return null;
        UserDTO dto = new UserDTO();
        dto.setUuid(user.getUuid());
        dto.setPseudo(user.getPseudo());
        dto.setEmail(user.getEmail());
        dto.setStatus(user.getStatus());
        return dto;
    }

    public static List<UserDTO> toDtoList(List<User> users) {
        if(Objects.isNull(users) || users.isEmpty()) return Collections.emptyList();
        return users.stream()
                .map(UserMapper::toDTO)
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }
}
