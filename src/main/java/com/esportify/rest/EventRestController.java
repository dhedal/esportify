package com.esportify.rest;

import com.esportify.dto.*;
import com.esportify.entity.User;
import com.esportify.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/events")
public class EventRestController extends BaseRestController{
    private static final Logger LOG = LoggerFactory.getLogger(EventRestController.class);

    private EventService eventService;

    @Autowired
    public EventRestController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/organizers")
    public ResponseEntity<List<UserDTO>> getOrganizers() {
        List<UserDTO> organizers = this.eventService.getAllOrganizers();
        return ResponseEntity.ok(organizers);
    }

    /**
     *
     * @param page
     * @param search
     * @param players
     * @param date
     * @param organizer
     * @return
     */
    @GetMapping("/filter")
    public ResponseEntity<EventsPageResponse> getEvents(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String players,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String organizer
    ) {
        LOG.debug("## getEvents");
        try {
            EventsPageResponse response = this.eventService.getFilteredEvents(page, search, players, date, organizer, 10);
            return ResponseEntity.ok(response);
        }catch(Exception e) {
            LOG.error(e.getMessage());
            return ResponseEntity.internalServerError().body(new EventsPageResponse());
        }
    }

    @GetMapping("/my-events")
    public ResponseEntity<?> getMyEvents(@AuthenticationPrincipal(errorOnInvalidType=true) User user) {
        LOG.debug("## getMyEvents(@AuthenticationPrincipal(errorOnInvalidType=true) User user)");
        Response response = new Response();
        if(user == null) {
            response.addMessage("Veuiller vous reconnecter !");
            response.setAuthenticated(false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        try {
            List<EventDTO> events = this.eventService.getEventsByOrganizer(user);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            response.addMessage("Une erreur est survenue!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/start-event")
    public ResponseEntity<?> startEvent(@RequestBody UUIDRequest request) {
        LOG.debug("## startEvent(@RequestBody UUIDRequest eventUuid)");
        Response response = new Response();
        try {
            response = this.eventService.startEvent(request, response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            response.addMessage("Une erreur est survenue !");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping(value = "/event", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createEvent(@RequestBody EventRequest request, @AuthenticationPrincipal(errorOnInvalidType=true) User user) {
        LOG.debug("## @RequestBody EventRequest request, @AuthenticationPrincipal(errorOnInvalidType=true) User user)");
        Response response = new Response();
        response.setAuthenticated(true);
        if(user == null) {
            response.addMessage("Veuiller vous reconnecter !");
            response.setAuthenticated(false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            response = this.eventService.createEvent(request, response, user);
            return ResponseEntity.ok(response);

        } catch(Exception e) {
            LOG.error(e.getMessage());
            response.addMessage("Une erreur est survenue !");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
