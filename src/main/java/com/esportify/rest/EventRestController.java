package com.esportify.rest;

import com.esportify.dto.EventDTO;
import com.esportify.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventRestController extends BaseRestController{
    private static final Logger LOG = LoggerFactory.getLogger(EventRestController.class);

    private EventService eventService;

    @Autowired
    public EventRestController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        LOG.debug("## getAllEvents()");
        try {
            return ResponseEntity.ok(this.eventService.getUpcomingAndOngoingEvents());
        }catch(Exception e) {
            LOG.error(e.getMessage());
        }
        return ResponseEntity.ok(Collections.EMPTY_LIST);
    }
}
