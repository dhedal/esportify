package com.esportify.dto;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Response {
    private boolean ok = false;
    private List<String> messages = new ArrayList<>();


    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public void addMessage(String message) {
        if(StringUtils.hasText(message)) this.messages.add(message);
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }


}
