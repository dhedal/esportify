package com.esportify.dto;

import com.esportify.validation.ValidUUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UserStatusRequest {
    @NotBlank(message = "L'uuid est obligatoire")
    @ValidUUID(message = "L'uuid doit être valide")
    private String userUuid;
    @NotNull(message="La clé de status est obligatoire")
    private Integer statusKey;

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public Integer getStatusKey() {
        return statusKey;
    }

    public void setStatusKey(Integer statusKey) {
        this.statusKey = statusKey;
    }
}
