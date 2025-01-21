package com.esportify.service;

import com.esportify.dto.EventParticipantRequest;
import com.esportify.dto.Response;
import com.esportify.entity.Event;
import com.esportify.entity.EventParticipant;
import com.esportify.entity.User;
import com.esportify.enumerations.EventParticipantStatus;
import com.esportify.enumerations.EventStatus;
import com.esportify.repository.EventParticipantRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class EventParticipantService {
    private static final Logger LOG = LoggerFactory.getLogger(EventParticipantService.class);

    private EventParticipantRepository eventParticipantRepository;
    private Validator validator;
    private EventService eventService;

    @Autowired
    public EventParticipantService(
            EventParticipantRepository eventParticipantRepository,
            Validator validator,
            EventService eventService) {
        this.eventParticipantRepository = eventParticipantRepository;
        this.validator = validator;
        this.eventService = eventService;
    }

    /**
     *
     * @param request
     * @param response
     * @param participant
     * @return
     */
    @Transactional
    public Response jointEvent(EventParticipantRequest request, Response response, User participant) {
        LOG.debug("## jointEvent(EventParticipantRequest request, User participant)");

        if (Objects.isNull(request)) {
            throw new IllegalArgumentException("EventParticipantRequest ne doit pas être null");
        }
        if (Objects.isNull(response)) {
            throw new IllegalArgumentException("Response ne doit pas être null");
        }

        if (Objects.isNull(participant) || Objects.isNull(participant.getId())) {
            throw new IllegalArgumentException("Le participant de l'événement est obligatoire");
        }

        Set<ConstraintViolation<EventParticipantRequest>> violations = this.validator.validate(request);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<EventParticipantRequest> violation : violations) {
                response.addMessage(violation.getMessage());
            }
            return response;
        }

        Event event = this.eventService.getWithParticipantsByUuid(request.getUuid());
        System.out.println("*********************************");
        if(event == null) {
            response.addMessage("Cet événement n'existe pas");
            return response;
        }
        if(Objects.equals(event.getStatus(), EventStatus.PENDING)) {
            response.addMessage("Cet événement ne prend pas encore de participant");
            return response;
        }
        else if(Objects.equals(event.getStatus(), EventStatus.VALIDATED)) {
            List<EventParticipant> participants = event.getParticipants();
            if(participants.size() >= event.getMaxPlayers()) {
                this.eventService.changeEventStatus(event, EventStatus.FULL);
                response.addMessage("Le nombre maximum de participants est déjà atteint");
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

        EventParticipant eventParticipant = new EventParticipant();
        eventParticipant.setEvent(event);
        eventParticipant.setParticipant(participant);
        eventParticipant.setStatus(EventParticipantStatus.PENDING);
        eventParticipant = this.eventParticipantRepository.save(eventParticipant);


        response.setOk(
            !(
                Objects.isNull(eventParticipant) ||
                Objects.isNull(eventParticipant.getId()) ||
                Objects.isNull(eventParticipant.getUuid())
            )
        );

        return response;
    }


}
