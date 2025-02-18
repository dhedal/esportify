package com.esportify.rest;

import com.esportify.dto.ParticipantRejectRequest;
import com.esportify.dto.ParticipantsResponse;
import com.esportify.dto.Response;
import com.esportify.dto.UUIDRequest;
import com.esportify.entity.User;
import com.esportify.service.AuthenticationService;
import com.esportify.service.EventParticipantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/event-participant")
public class EventParticipantRestController extends BaseRestController{
    private static final Logger LOG = LoggerFactory.getLogger(EventParticipantRestController.class);

    private EventParticipantService eventParticipantService;
    @Autowired
    public EventParticipantRestController(
            EventParticipantService eventParticipantService,
            AuthenticationService authenticationService) {
        this.eventParticipantService = eventParticipantService;
    }

    @PostMapping("/join")
    public ResponseEntity<Response> joinEvent(@RequestBody UUIDRequest request,
                                              @AuthenticationPrincipal(errorOnInvalidType=true) User user) {
        LOG.debug("## joinEvent(@RequestBody UUIDRequest request, @AuthenticationPrincipal User user)");
        Response response = new Response();
        if(user == null) {
            response.addMessage("Veuiller vous reconnecter !");
            response.setAuthenticated(false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        try {
            response = this.eventParticipantService.jointEvent(request, response, user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            response.addMessage("Une erreur est survenue!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    @PostMapping("/leave")
    public ResponseEntity<Response> leaveEvent(@RequestBody UUIDRequest request,
                                              @AuthenticationPrincipal(errorOnInvalidType=true) User user) {
        LOG.debug("## leaveEvent(@RequestBody UUIDRequest request, @AuthenticationPrincipal User user)");
        Response response = new Response();
        if(user == null) {
            response.addMessage("Veuiller vous reconnecter !");
            response.setAuthenticated(false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        try {
            response = this.eventParticipantService.leaveEvent(request, response, user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            response.addMessage("Une erreur est survenue!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    @PostMapping("/participants")
    public ResponseEntity<ParticipantsResponse> getParticipantsByEvent(@RequestBody UUIDRequest request) {
        LOG.debug("## getParticipantsByEvent(@RequestBody UUIDRequest request)");
        ParticipantsResponse response = new ParticipantsResponse();
        try {
            response = this.eventParticipantService.getParticipants(request, response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            response.addMessage("Une erreur est survenue!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/reject")
    public ResponseEntity<?> rejectParticipant(@RequestBody ParticipantRejectRequest request) {
        LOG.debug("## rejectParticipant(@RequestBody ParticipantRejectRequest request)");
        Response response = new Response();
        try {
            response = this.eventParticipantService.rejectParticipant(request, response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            response.addMessage("Une erreur est survenue!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
