package com.esportify.service;

import com.esportify.dto.EventDTO;
import com.esportify.dto.EventRequest;
import com.esportify.dto.RegisterRequest;
import com.esportify.dto.Response;
import com.esportify.entity.Event;
import com.esportify.entity.User;
import com.esportify.enumerations.EventStatus;
import com.esportify.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Objects;
import java.util.Set;

@Service
public class EventService {

    private static final Logger LOG = LoggerFactory.getLogger(EventService.class);

    private EventRepository eventRepository;
    private Validator validator;

    @Autowired
    public EventService(
            EventRepository eventRepository,
            Validator validator) {
        this.eventRepository = eventRepository;
        this.validator = validator;
    }

    /**
     *
     * @param request
     * @param response
     * @param organizer
     * @return
     */
    public Response createEvent(EventRequest request, Response response, User organizer) {
        LOG.debug("## createEvent(EventRequest request, Response response, User organizer)");

        if (Objects.isNull(request)) {
            throw new IllegalArgumentException("EventRequest ne doit pas être null");
        }
        if (Objects.isNull(response)) {
            throw new IllegalArgumentException("Response ne doit pas être null");
        }

        if (Objects.isNull(organizer) || Objects.isNull(organizer.getId())) {
            throw new IllegalArgumentException("L'organisateur de l'événement est obligatoire");
        }

        Set<ConstraintViolation<EventRequest>> violations = this.validator.validate(request);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<EventRequest> violation : violations) {
                response.addMessage(violation.getMessage());
            }
            return response;
        }

        Event event = new Event();
        event.setDescription(request.getDescription());
        event.setMaxPlayers(request.getMaxPlayers());
        event.setStartDateTime(request.getStartDateTime());
        event.setEndDateTime(request.getEndDateTime());
        event.setOrganizer(organizer);
        event.setStatus(EventStatus.PENDING);
        event = this.eventRepository.save(event);

        response.setOk(
                !(Objects.isNull(event) || Objects.isNull(event.getId()) || Objects.isNull(event.getUuid()))
        );

        return response;
    }

}
