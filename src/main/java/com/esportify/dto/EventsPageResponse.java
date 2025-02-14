package com.esportify.dto;

import java.util.List;

public class EventsPageResponse extends Response {
    private int totalPages;
    private List<EventDTO> events;

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<EventDTO> getEvents() {
        return events;
    }

    public void setEvents(List<EventDTO> events) {
        this.events = events;
    }
}
