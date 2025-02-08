package com.esportify.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class PasswordRequest {
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit avoir au moins 8 caractères.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z]).+$", message = "Le mot de passe doit contenir à la fois des majuscules et des minuscules.")
    @Pattern(regexp = ".*\\d.*", message = "Le mot de passe doit contenir au moins un chiffre")
    @Pattern(regexp = ".*[!@#$%&*?:+-_].*", message = "Le mot de passe doit contenir au moins un caractère spécial")
    private String passwordHold;
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit avoir au moins 8 caractères.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z]).+$", message = "Le mot de passe doit contenir à la fois des majuscules et des minuscules.")
    @Pattern(regexp = ".*\\d.*", message = "Le mot de passe doit contenir au moins un chiffre")
    @Pattern(regexp = ".*[!@#$%&*?:+-_].*", message = "Le mot de passe doit contenir au moins un caractère spécial")
    private String passwordNew;

    public String getPasswordHold() {
        return passwordHold;
    }

    public void setPasswordHold(String passwordHold) {
        this.passwordHold = passwordHold;
    }

    public String getPasswordNew() {
        return passwordNew;
    }

    public void setPasswordNew(String passwordNew) {
        this.passwordNew = passwordNew;
    }
}
