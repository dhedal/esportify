package com.esportify.dto;

public class RegisterResponse extends Response{

    private UserDTO userDTO;

    private boolean emailSent;

    public RegisterResponse() {
        super();
    }

    public UserDTO getUserDTO() {
        return userDTO;
    }

    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
    }

    public boolean isEmailSent() {
        return emailSent;
    }

    public void setEmailSent(boolean emailSent) {
        this.emailSent = emailSent;
    }
}
