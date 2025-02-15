package com.esportify.dto;

import java.util.List;

public class AsksPageResponse extends Response{
    private int totalPages;
    private List<AskDTO> asks;

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<AskDTO> getAsks() {
        return asks;
    }

    public void setAsks(List<AskDTO> asks) {
        this.asks = asks;
    }
}
