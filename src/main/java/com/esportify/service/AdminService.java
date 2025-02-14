package com.esportify.service;

import com.esportify.dto.EventStatusRequest;
import com.esportify.dto.RegisterRequest;
import com.esportify.dto.Response;
import com.esportify.dto.UserStatusRequest;
import com.esportify.entity.Event;
import com.esportify.entity.User;
import com.esportify.enumerations.EventStatus;
import com.esportify.enumerations.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Objects;
import java.util.Set;

@Service
public class AdminService {
    private static final Logger LOG = LoggerFactory.getLogger(AdminService.class);
    private AskService askService;
    private UserService userService;
    private EventService eventService;
    private Validator validator;
    public AdminService(
            AskService askService,
            UserService userService,
            EventService eventService,
            Validator validator) {
        this.askService = askService;
        this.userService = userService;
        this.eventService = eventService;
        this.validator = validator;

    }

    /**
     *
     * @param request
     * @param response
     * @return
     */
    public Response changeUserStatus(UserStatusRequest request, Response response) {
        LOG.debug("## changeUserStatus(UserStatusRequest request, Response response)");

        if(Objects.isNull(request)){
            throw new IllegalArgumentException("UserStatusRequest ne doit pas être null");
        }
        if(Objects.isNull(response)){
            throw new IllegalArgumentException("Response ne doit pas être null");
        }

        Set<ConstraintViolation<UserStatusRequest>> violations = this.validator.validate(request);
        if(!violations.isEmpty()) {
            for(ConstraintViolation<UserStatusRequest> violation : violations) {
                response.addMessage(violation.getMessage());
            }
            return response;
        }

        User user = this.userService.getByUuid(request.getUserUuid());
        if(user == null) {
            response.addMessage("L'utilisateur est introuvable !");
            return response;
        }

        if(Objects.isNull(request.getStatusKey())) {
            response.addMessage("Ce status n'existe pas");
            return response;
        }

        UserStatus newStatus = UserStatus.getByKey(request.getStatusKey());
        if(Objects.equals(user.getStatus(), newStatus)) {
            response.addMessage("Vous avez déjà ce status");
            return response;
        }

        this.userService.changeStatus(user, newStatus);
        response.setOk(true);
        return response;
    }


    /**
     *
     * @param request
     * @param response
     * @return
     */
    public Response changeEventStatus(EventStatusRequest request, Response response) {
        LOG.debug("## changeUserStatus(EventStatusRequest request, Response response)");

        if(Objects.isNull(request)){
            throw new IllegalArgumentException("EventStatusRequest ne doit pas être null");
        }
        if(Objects.isNull(response)){
            throw new IllegalArgumentException("Response ne doit pas être null");
        }

        Set<ConstraintViolation<EventStatusRequest>> violations = this.validator.validate(request);
        if(!violations.isEmpty()) {
            for(ConstraintViolation<EventStatusRequest> violation : violations) {
                response.addMessage(violation.getMessage());
            }
            return response;
        }

        Event event = this.eventService.getEventByUuid(request.getEventUuid());
        if(event == null) {
            response.addMessage("L'évent est introuvable !");
            return response;
        }

        EventStatus eventStatus = EventStatus.getByKey(request.getStatusKey());
        if(Objects.equals(eventStatus, event.getStatus())) {
            response.addMessage("Cet événement possède déjà ce status !");
            return response;
        }

        this.eventService.changeEventStatus(event, eventStatus);
        response.setOk(true);
        return response;
    }
}
