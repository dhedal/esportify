package com.esportify.dto;

public class LoginResponse extends Response{
    private UserDTO userDTO;

    public LoginResponse() { super();}

    public UserDTO getUserDTO() {
        return userDTO;
    }

    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
    }
}
