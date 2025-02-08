package com.esportify.rest;

import com.esportify.dto.AskRequest;
import com.esportify.dto.Response;
import com.esportify.entity.Ask;
import com.esportify.entity.User;
import com.esportify.enumerations.AskType;
import com.esportify.service.AskService;
import com.esportify.service.EventParticipantService;
import com.esportify.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/user")
public class UserRestController extends BaseRestController{
    private static final Logger LOG = LoggerFactory.getLogger(UserRestController.class);

    private UserService userService;
    private AskService askService;
    private EventParticipantService eventParticipantService;

    @Autowired
    public UserRestController(
            UserService userService,
            EventParticipantService eventParticipantService,
            AskService askService) {
        this.userService = userService;
        this.eventParticipantService = eventParticipantService;
        this.askService = askService;
    }

    @GetMapping("/my-events")
    public ResponseEntity<?> getEventsForCurrentUser(@AuthenticationPrincipal User user) {
        LOG.debug("## getEventsForCurrentUser(@AuthenticationPrincipal User user)");
        try {
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            return ResponseEntity.ok(this.eventParticipantService.getEventsByParticipant(user));
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return ResponseEntity.ok(Collections.EMPTY_LIST);
    }

    @GetMapping("/request-organizer")
    public ResponseEntity<?> requestOrganizerStatus(@AuthenticationPrincipal User user) {
        LOG.debug("## requestOrganizerStatus(@AuthenticationPrincipal User user)");
        Response response = new Response();
        try {
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            AskRequest askRequest = new AskRequest();
            askRequest.setType(AskType.ASK_ORGANIZER);
            response = this.askService.requestOrganizerStatus(askRequest, new Response(), user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

    }

}
