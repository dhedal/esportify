package com.esportify.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class RegisterRequest extends LoginRequest{
    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 50, message = "le nom doit avoir entre 3 et 50 charactères")
    private String name;

    public RegisterRequest() { super();}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
