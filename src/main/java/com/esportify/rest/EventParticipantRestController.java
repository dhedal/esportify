package com.esportify.rest;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
