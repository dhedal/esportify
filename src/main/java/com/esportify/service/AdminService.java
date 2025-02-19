package com.esportify.service;

import com.esportify.dto.*;
import com.esportify.entity.Ask;
import com.esportify.entity.Event;
import com.esportify.entity.User;
import com.esportify.enumerations.AskStatus;
import com.esportify.enumerations.AskType;
import com.esportify.enumerations.EventStatus;
import com.esportify.enumerations.UserStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     *
     * @param request
     * @param response
     * @return
     */
    @Transactional
    public Response changeAskStatus(ChangeAskStatusRequest request, Response response) {
        LOG.debug("## changeAskStatus(ChangeAskStatusRequest request, Response response)");

        if(Objects.isNull(request)){
            throw new IllegalArgumentException("ChangeAskStatusRequest ne doit pas être null");
        }
        if(Objects.isNull(response)){
            throw new IllegalArgumentException("Response ne doit pas être null");
        }

        Set<ConstraintViolation<ChangeAskStatusRequest>> violations = this.validator.validate(request);
        if(!violations.isEmpty()) {
            for(ConstraintViolation<ChangeAskStatusRequest> violation : violations) {
                response.addMessage(violation.getMessage());
            }
            return response;
        }

        Ask ask = this.askService.findByUuid(request.getUuid());
        if(ask == null) {
            response.addMessage("La demande n'existe pas");
            return response;
        }

        if (ask.getStatus() != AskStatus.PENDING) {
            response.addMessage("Cette demande a déjà été traitée.");
            return response;
        }

        AskStatus newAskStatus = AskStatus.getByKey(request.getStatusKey());
        if(Objects.equals(newAskStatus, AskStatus.UNDEFINED)) {
            response.addMessage("Le status demandé est inconnu !");
            return response;
        }
        else if(Objects.equals(newAskStatus, AskStatus.PENDING)) {
            if(Objects.equals(ask.getStatus(), AskStatus.PENDING)) {
                response.addMessage("La démande est déja en attente de validation");
                return response;
            }
        }

        User author = ask.getAuthor();
        if(author == null) {
            response.addMessage("L'auteur de cette demande n'existe pas");
            return response;
        }

        if(Objects.equals(ask.getType(), AskType.ASK_ORGANIZER)) {
            return this.changeStatusPlayerToOrganizer(author, ask, newAskStatus, response);
        }

        ask.setStatus(newAskStatus);
        this.askService.save(ask);
        response.setOk(true);
        return response;

    }

    private Response changeStatusPlayerToOrganizer(User author, Ask ask, AskStatus newAskStatus, Response response) {
        if(author == null) throw new IllegalArgumentException("le paramètre author est null !");
        if(ask == null) throw new IllegalArgumentException("le paramètre ask est null !");
        if(newAskStatus == null) throw new IllegalArgumentException("le newAskStatus ask est null !");
        if(response == null) throw new IllegalArgumentException("le response ask est null !");

        if (ask.getType() != AskType.ASK_ORGANIZER) {
            response.addMessage("Cette demande ne concerne pas un passage au statut ORGANIZER.");
            return response;
        }


        if(Objects.equals(author.getStatus(), UserStatus.ORGANIZER)) {
            response.addMessage("Cet utilisateur est déjà ORGANIZER.");
            return response;
        }

        if(Objects.equals(newAskStatus, AskStatus.APPROVED)) {
            this.userService.changeStatus(author, UserStatus.ORGANIZER);
            ask.setStatus(newAskStatus);
            this.askService.save(ask);
            response.setOk(true);
            return response;
        }
        else if(Objects.equals(newAskStatus, AskStatus.REJECTED)) {
            ask.setStatus(newAskStatus);
            this.askService.save(ask);
            response.setOk(true);
            return response;
        }

        response.addMessage("Cette demande doit être uniquement approuvée ou refusée !");
        return response;

    }
}
