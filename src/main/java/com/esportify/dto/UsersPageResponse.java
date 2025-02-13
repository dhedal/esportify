package com.esportify.dto;

import java.util.List;

public class UsersPageResponse  extends Response{

    private int totalPages;
    private List<UserDTO> users;

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<UserDTO> getUsers() {
        return users;
    }

    public void setUsers(List<UserDTO> users) {
        this.users = users;
    }
}
