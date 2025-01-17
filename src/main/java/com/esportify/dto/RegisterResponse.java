package com.esportify.dto;

public class RegisterResponse extends LoginResponse{

    private boolean emailSent;

    public RegisterResponse() {
        super();
    }

    public boolean isEmailSent() {
        return emailSent;
    }

    public void setEmailSent(boolean emailSent) {
        this.emailSent = emailSent;
    }
}
