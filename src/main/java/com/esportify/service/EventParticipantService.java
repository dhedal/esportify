package com.esportify.service;

import com.esportify.dto.*;
import com.esportify.entity.Event;
import com.esportify.entity.EventParticipant;
import com.esportify.entity.User;
import com.esportify.enumerations.EventParticipantStatus;
import com.esportify.enumerations.EventStatus;
import com.esportify.mapper.EventMapper;
import com.esportify.mapper.EventParticipantMapper;
import com.esportify.mapper.ParticipantMapper;
import com.esportify.repository.EventParticipantRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class EventParticipantService {
    private static final Logger LOG = LoggerFactory.getLogger(EventParticipantService.class);

    private EventParticipantRepository eventParticipantRepository;
    private Validator validator;
    private EventService eventService;
    private UserService userService;

    @Autowired
    public EventParticipantService(
            EventParticipantRepository eventParticipantRepository,
            Validator validator,
            EventService eventService,
            UserService userService) {
        this.eventParticipantRepository = eventParticipantRepository;
        this.validator = validator;
        this.eventService = eventService;
        this.userService = userService;
    }

    /**
     *
     * @param request
     * @param response
     * @param participant
     * @return
     */
    @Transactional
    public Response jointEvent(UUIDRequest request, Response response, User participant) {
        LOG.debug("## jointEvent(EventParticipantRequest request, User participant)");

        if (Objects.isNull(request)) {
            throw new IllegalArgumentException("EventParticipantRequest ne doit pas être null");
        }
        if (Objects.isNull(response)) {
            throw new IllegalArgumentException("Response ne doit pas être null");
        }

        if (Objects.isNull(participant) || participant.isNew()) {
            throw new IllegalArgumentException("Le participant de l'événement est obligatoire");
        }

        Set<ConstraintViolation<UUIDRequest>> violations = this.validator.validate(request);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<UUIDRequest> violation : violations) {
                response.addMessage(violation.getMessage());
            }
            return response;
        }

        Event event = this.eventService.getWithParticipantsByUuid(request.getUuid());
        if(event == null) {
            response.addMessage("Cet événement n'existe pas");
            return response;
        }
        else if(Objects.equals(event.getStatus(), EventStatus.PENDING)) {
            response.addMessage("Cet événement ne prend pas encore de participant");
            return response;
        }
        else if(Objects.equals(event.getStatus(), EventStatus.CANCELLED) ||
                Objects.equals(event.getStatus(), EventStatus.CLOSED)) {
            response.addMessage("Cet événement est annulé ou fermé !");
            return response;
        }
        else if(Objects.equals(event.getStatus(), EventStatus.ON_GOING)) {
            response.addMessage("Cet événement est déja en cour !");
            return response;
        }
        else if(Objects.equals(event.getStatus(), EventStatus.VALIDATED)) {
            List<EventParticipant> participants = event.getParticipants();
            if(participants.size() >= event.getMaxPlayers()) {
                this.eventService.changeEventStatus(event, EventStatus.FULL);
                response.addMessage("Le nombre maximum de participants est déjà atteint !");
                return response;
            }
        }
        else if( Objects.equals(event.getStatus(), EventStatus.FULL)){
            response.addMessage("Le nombre maximum de participants est déjà atteint");
            return response;
        }
        else {
            response.addMessage("Cet évenement ne prend pas de participant");
            return response;
        }

        EventParticipant existParticipant = this.eventParticipantRepository.findByEventAndParticipant(event, participant);

        if(existParticipant != null) {
            if(Objects.equals(existParticipant.getStatus(), EventParticipantStatus.REJECTED)) {
                response.addMessage("Votre demande de participation a été rejeté.");
                return response;
            }
            else if(Objects.equals(existParticipant.getStatus(), EventParticipantStatus.BANNED)) {
                response.addMessage("Vous avez été banni de cet événement.");
                return response;
            }

            existParticipant.setStatus(EventParticipantStatus.APPROVED);
            this.eventParticipantRepository.save(existParticipant);
            response.setOk(true);
            return response;
        }

        EventParticipant eventParticipant = new EventParticipant();
        eventParticipant.setEvent(event);
        eventParticipant.setParticipant(participant);
        eventParticipant.setStatus(EventParticipantStatus.APPROVED);
        this.eventParticipantRepository.save(eventParticipant);

        response.setOk(true);
        return response;
    }

    /**
     *
     * @param participant
     * @return
     */
    public List<EventParticipantDTO> getEventsByParticipant(User participant) {
        LOG.debug("## getEventsByParticipant(User participant)");
        if(participant == null) return Collections.EMPTY_LIST;
        List<EventParticipant> eventParticipants = this.eventParticipantRepository.findByParticipant(participant);
        return EventParticipantMapper.toDTOList(eventParticipants);
    }

    /**
     *
     * @param request
     * @param response
     * @return
     */
    public ParticipantsResponse getParticipants(UUIDRequest request, ParticipantsResponse response) {
        LOG.debug("## jointEvent(UUIDRequest request, ParticipantsResponse response)");

        if (Objects.isNull(request)) {
            throw new IllegalArgumentException("UUIDRequest ne doit pas être null");
        }
        if (Objects.isNull(response)) {
            throw new IllegalArgumentException("ParticipantsResponse ne doit pas être null");
        }

        Set<ConstraintViolation<UUIDRequest>> violations = this.validator.validate(request);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<UUIDRequest> violation : violations) {
                response.addMessage(violation.getMessage());
            }
            return response;
        }

        Event event = this.eventService.getEventByUuid(request.getUuid());
        if(event == null) {
            response.addMessage("Cet événement est introuvable !");
            return response;
        }

        List<EventParticipant> eventParticipants = this.eventParticipantRepository.findAllByEvent(event);
        response.setParticipants(ParticipantMapper.toDTOList(eventParticipants));
        response.setEvent(EventMapper.toDTO(event));
        response.setOk(true);
        return response;
    }

    /**
     *
     * @param request
     * @param response
     * @return
     */
    public Response rejectParticipant(ParticipantRejectRequest request, Response response) {
        LOG.debug("## jointEvent(UUIDRequest request, ParticipantsResponse response)");

        if (Objects.isNull(request)) {
            throw new IllegalArgumentException("UUIDRequest ne doit pas être null");
        }
        if (Objects.isNull(response)) {
            throw new IllegalArgumentException("ParticipantsResponse ne doit pas être null");
        }

        Set<ConstraintViolation<ParticipantRejectRequest>> violations = this.validator.validate(request);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<ParticipantRejectRequest> violation : violations) {
                response.addMessage(violation.getMessage());
            }
            return response;
        }

        Event event = this.eventService.getWithParticipantsByUuid(request.getEventUuid());
        if(event == null) {
            response.addMessage("Cet événement est introuvable !");
            return response;
        }
        if(Objects.equals(event.getStatus(), EventStatus.CANCELLED) ||
                Objects.equals(event.getStatus(), EventStatus.CLOSED)) {
            response.addMessage("Cet événement est annulé ou fermé !");
            return response;
        }

        User participant = this.userService.getByUuid(request.getParticipantUuid());
        if(participant == null) {
            response.addMessage("Ce participant est introuvable !");
            return response;
        }


        EventParticipant existParticipant = this.eventParticipantRepository.findByEventAndParticipant(event, participant);
        if(existParticipant == null) {
            response.addMessage("Ce participant n'est pas inscrit à cet événement !");
            return response;
        }

        if(Objects.equals(existParticipant.getStatus(), EventParticipantStatus.REJECTED)) {
            response.addMessage("Le demande de participation est déjà rejetée.");
            response.setOk(false);
            return response;
        }

        if(Objects.equals(existParticipant.getStatus(), EventParticipantStatus.BANNED)) {
            response.addMessage("Le participant est déja banni.");
            response.setOk(false);
            return response;
        }

        existParticipant.setStatus(EventParticipantStatus.REJECTED);
        this.eventParticipantRepository.save(existParticipant);
        response.addMessage("Le participant est banni de cet événement !");
        response.setOk(true);
        return response;
    }

    /**
     *
     * @param request
     * @param response
     * @param participant
     * @return
     */
    public Response leaveEvent(UUIDRequest request, Response response, User participant) {
        LOG.debug("## leaveEvent(UUIDRequest request, User participant)");

        if (Objects.isNull(request)) {
            throw new IllegalArgumentException("UUIDRequest ne doit pas être null");
        }
        if (Objects.isNull(response)) {
            throw new IllegalArgumentException("Response ne doit pas être null");
        }

        if (Objects.isNull(participant) || participant.isNew()) {
            throw new IllegalArgumentException("Le participant de l'événement est obligatoire");
        }

        Set<ConstraintViolation<UUIDRequest>> violations = this.validator.validate(request);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<UUIDRequest> violation : violations) {
                response.addMessage(violation.getMessage());
            }
            return response;
        }

        Event event = this.eventService.getWithParticipantsByUuid(request.getUuid());
        if(event == null) {
            response.addMessage("Cet événement n'existe pas");
            return response;
        }
        if(!Objects.equals(event.getStatus(), EventStatus.VALIDATED ) &&
                !Objects.equals(event.getStatus(), EventStatus.FULL)) {
            response.addMessage("La désinscription est impossible car cet événement est " +
                    event.getStatus().getLabel());
            return response;
        }


        EventParticipant existParticipant = this.eventParticipantRepository.findByEventAndParticipant(event, participant);
        if(Objects.equals(existParticipant.getStatus(), EventParticipantStatus.REJECTED)) {
            response.addMessage("Vous avez déjà été banni.");
            response.setOk(false);
            return response;
        }

        existParticipant.setStatus(EventParticipantStatus.WITHDRAWN);
        this.eventParticipantRepository.save(existParticipant);
        response.addMessage("Le participant est désinscrit de cet événement !");
        response.setOk(true);
        return response;

    }
}
