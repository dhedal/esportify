package com.esportify.mapper;

import com.esportify.dto.UserDTO;
import com.esportify.entity.User;

import java.util.Objects;

public class UserMapper {

    public static UserDTO toDTO(User user) {
        if(Objects.isNull(user)) throw new IllegalArgumentException("user ne doit pas être null");
        UserDTO dto = new UserDTO();
        dto.setUuid(user.getUuid());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAdmin(user.isAdmin());
        return dto;
    }
}
