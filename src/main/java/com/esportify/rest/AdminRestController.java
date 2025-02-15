package com.esportify.rest;

import com.esportify.dto.*;
import com.esportify.entity.User;
import com.esportify.enumerations.AskStatus;
import com.esportify.enumerations.AskType;
import com.esportify.enumerations.EventStatus;
import com.esportify.enumerations.UserStatus;
import com.esportify.service.AdminService;
import com.esportify.service.AskService;
import com.esportify.service.EventService;
import com.esportify.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController  extends BaseRestController{
    private static final Logger LOG = LoggerFactory.getLogger(AdminRestController.class);

    private UserService userService;
    private AdminService adminService;
    private EventService eventService;
    private AskService askService;

    @Autowired
    public AdminRestController(
            UserService userService,
            AdminService adminService,
            EventService eventService,
            AskService askService) {
        this.userService = userService;
        this.adminService = adminService;
        this.eventService = eventService;
        this.askService = askService;
    }

    /**
     *
     * @param page
     * @param search
     * @param status
     * @param admin
     * @return
     */
    @GetMapping("/users")
    public ResponseEntity<UsersPageResponse> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal User admin) {
        LOG.debug("## getAllUsers");
        if (admin == null || !admin.getStatus().equals(UserStatus.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new UsersPageResponse());
        }

        try {
            UserStatus userStatus = null;
            if(StringUtils.hasText(status)) {
                try {
                    int userStatusKey = Integer.parseInt(status);
                    userStatus = UserStatus.getByKey(userStatusKey);
                } catch (NumberFormatException e) {
                    LOG.debug(e.getMessage());
                }
            }
            UsersPageResponse response = this.userService.getPageUsers(page, search, userStatus,10);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            LOG.error(e.getMessage());
            UsersPageResponse response = new UsersPageResponse();
            response.addMessage("Une erreur est survenue !");
            response.setOk(false);
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     *
     * @param page
     * @param search
     * @param status
     * @param admin
     * @return
     */
    @GetMapping("/events")
    public ResponseEntity<EventsPageResponse> getAllEvents(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal User admin) {
        LOG.debug("## getAllEvents");
        if (admin == null || !admin.getStatus().equals(UserStatus.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new EventsPageResponse());
        }

        try {
            EventStatus eventStatus = null;
            if(StringUtils.hasText(status)) {
                try {
                    int eventStatusKey = Integer.parseInt(status);
                    eventStatus = EventStatus.getByKey(eventStatusKey);
                } catch (NumberFormatException e) {
                    LOG.debug(e.getMessage());
                }
            }
            EventsPageResponse response = this.eventService.getPageEvents(page, search, eventStatus,10);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            LOG.error(e.getMessage());
            EventsPageResponse response = new EventsPageResponse();
            response.addMessage("Une erreur est survenue !");
            response.setOk(false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }
    }

    @GetMapping("/asks")
    public ResponseEntity<AsksPageResponse> getAsks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal User admin) {
        LOG.debug("## getAsks");
        if (admin == null || !admin.getStatus().equals(UserStatus.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AsksPageResponse());
        }

        try {
            AskType askType = null;
            if(StringUtils.hasText(type)) {
                try {
                    askType = AskType.getByKey(Integer.parseInt(type));
                } catch (NumberFormatException e) {
                    LOG.debug(e.getMessage());
                }
            }
            AskStatus askStatus = null;
            if(StringUtils.hasText(status)) {
                try {
                    askStatus = AskStatus.getByKey(Integer.parseInt(status));
                } catch (NumberFormatException e) {
                    LOG.debug(e.getMessage());
                }
            }
            AsksPageResponse response = this.askService.getPageAsks(page, askType, askStatus, 10);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            LOG.error(e.getMessage());
            AsksPageResponse response = new AsksPageResponse();
            response.addMessage("Une erreur est survenue !");
            response.setOk(false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }
    }

    /**
     *
     * @param request
     * @param admin
     * @return
     */
    @PutMapping ("/user-status")
    public ResponseEntity<Response> changeUserStatus(@RequestBody UserStatusRequest request, @AuthenticationPrincipal User admin) {
        LOG.debug("## getAllEvents(@RequestBody UserStatusRequest request, @AuthenticationPrincipal User admin)");
        Response response = new Response();
        if (admin == null || !admin.getStatus().equals(UserStatus.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }

        try {
            response = this.adminService.changeUserStatus(request, response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            response.addMessage("Une erreur est survenue !");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     *
     * @param request
     * @param admin
     * @return
     */
    @PutMapping ("/event-status")
    public ResponseEntity<Response> changeEventStatus(@RequestBody EventStatusRequest request, @AuthenticationPrincipal User admin) {
        LOG.debug("## changeEventStatus(@RequestBody EventStatusRequest request, @AuthenticationPrincipal User admin)");
        Response response = new Response();
        if (admin == null || !admin.getStatus().equals(UserStatus.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }

        try {
            response = this.adminService.changeEventStatus(request, response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            response.addMessage("Une erreur est survenue !");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     *
     * @param request
     * @param admin
     * @return
     */
    @PutMapping ("/ask-status")
    public ResponseEntity<Response> changeAskStatus(@RequestBody ChangeAskStatusRequest request, @AuthenticationPrincipal User admin) {
        LOG.debug("## changeAskStatus(@RequestBody ChangeAskStatusRequest request, @AuthenticationPrincipal User admin)");
        Response response = new Response();
        if (admin == null || !admin.getStatus().equals(UserStatus.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }
        try {
            response = this.adminService.changeAskStatus(request, response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            response.addMessage("Une erreur est survenue !");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     *
     * @param admin
     * @return
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getStats(@AuthenticationPrincipal User admin) {
        LOG.debug("## getStats(@AuthenticationPrincipal User admin)");
        return ResponseEntity.ok(Collections.emptyList());
    }
}
