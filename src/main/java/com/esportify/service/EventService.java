package com.esportify.service;

import com.esportify.dto.*;
import com.esportify.entity.Event;
import com.esportify.entity.EventParticipant;
import com.esportify.entity.User;
import com.esportify.enumerations.EventParticipantStatus;
import com.esportify.enumerations.EventStatus;
import com.esportify.enumerations.UserStatus;
import com.esportify.mapper.EventMapper;
import com.esportify.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.*;

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
     * @param organizer
     * @return
     */
    public List<EventDTO> listByOrganizer(User organizer) {
        LOG.debug("## list(User user)");
        if(organizer == null) throw new IllegalArgumentException("L'organisateur de l'événement est obligatoire");
        List<Event> events = this.eventRepository.findAllByOrganizer(organizer);
        return EventMapper.toDTOList(events);
    }

    /**
     *
     * @param eventUuid
     * @return
     */
    public Event getWithParticipantsByUuid(String eventUuid) {
        LOG.debug("## getWithParticipantsByUuid(String eventUuid)");
        return StringUtils.hasText(eventUuid) ?
                this.eventRepository.findWithParticipantsByUuid(eventUuid) :
                null;
    }

    /**
     *
     * @param uuid
     * @return
     */
    public Event getEventByUuid(String uuid) {
        return StringUtils.hasText(uuid) ?
                this.eventRepository.findByUuid(uuid) :
                null;
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

        if (Objects.isNull(organizer) || organizer.isNew()) {
            throw new IllegalArgumentException("L'organisateur de l'événement est obligatoire");
        }

        if(!(
                Objects.equals(UserStatus.ORGANIZER, organizer.getStatus()) ||
                !Objects.equals(UserStatus.ADMIN, organizer.getStatus())
        )) {
            response.addMessage("Vous ne possédez pas les droits pour créer un event !");
            return response;
        }

        Set<ConstraintViolation<EventRequest>> violations = this.validator.validate(request);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<EventRequest> violation : violations) {
                response.addMessage(violation.getMessage());
            }
            return response;
        }

        List<Event> checkList = this.eventRepository.findByTitle(request.getTitle());
        if(checkList != null && !checkList.isEmpty()) {
            for(Event event : checkList) {
                if(Objects.equals(event.getStartDateTime(), request.getStartDateTime())) {
                    response.addMessage("Ce titre est déja enregistré par un autre événement pour même date/heure que le votre");
                    response.setOk(false);
                    return response;
                }
            }
        }

        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setMaxPlayers(request.getMaxPlayers());
        event.setStartDateTime(request.getStartDateTime());
        event.setEndDateTime(request.getEndDateTime());
        event.setOrganizer(organizer);
        event.setStatus(EventStatus.PENDING);

        try {
            event = this.eventRepository.save(event);
            response.addMessage("L'événement est en attente de validation !");
            response.setOk(true);
        } catch (DataIntegrityViolationException e) {
            LOG.error("erreur lors de l'enregistrement d'un event", e);
            response.addMessage("Vos données son invalides, veuillez vérifier");
            response.setOk(false);
        }

        return response;
    }

    /**
     *
     * @param event
     * @param status
     */
    public boolean changeEventStatus(Event event, EventStatus status) {
        LOG.debug("## changeEventStatus(Event event, EventStatus status)");
        if(event == null || event.isNew() || status == null) return false;
        event.setStatus(status);
        this.eventRepository.save(event);
        return true;
    }

    /**
     *
     * @return
     */
    public List<EventDTO> getUpcomingAndOngoingEvents() {
        LOG.debug("## getUpcomingAndOngoingEvents()");
        List<Event> events = this.eventRepository.findUpcomingAndOngoingEvents(
                List.of(EventStatus.VALIDATED, EventStatus.ON_GOING, EventStatus.FULL));
        return EventMapper.toDTOList(events);
    }

    /**
     *
     * @param organizer
     * @return
     */
    public List<EventDTO> getEventsByOrganizer(User organizer) {
        LOG.debug("## getEventsByOrganizer(User organizer)");
        if(organizer == null) return Collections.EMPTY_LIST;
        List<Event> events = this.eventRepository.findByOrganizer(organizer);
        return EventMapper.toDTOList(events);
    }

    /**
     *
     * @param request
     * @param response
     * @return
     */
    public Response startEvent(UUIDRequest request, Response response) {
        LOG.debug("## createEvent(EventRequest request, Response response)");

        if (Objects.isNull(request)) {
            throw new IllegalArgumentException("UUIDRequest ne doit pas être null");
        }
        if (Objects.isNull(response)) {
            throw new IllegalArgumentException("Response ne doit pas être null");
        }

        Set<ConstraintViolation<UUIDRequest>> violations = this.validator.validate(request);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<UUIDRequest> violation : violations) {
                response.addMessage(violation.getMessage());
            }
            return response;
        }

        Event event = this.eventRepository.findByUuid(request.getUuid());
        if(event == null) {
            response.addMessage("C'événement est introuvable !");
            return response;
        }

        response.setOk(this.changeEventStatus(event, EventStatus.ON_GOING));
        return response;
    }

    /**
     *
     * @return
     */
    public List<EventDTO> getAllEvents() {
        LOG.debug("## getAllEvents()");
        List<Event> events = this.eventRepository.findAll();
        return EventMapper.toDTOList(events);
    }

    /**
     *
     * @param page
     * @param search
     * @param eventStatus
     * @param pageSize
     * @return
     */
    public EventsPageResponse getPageEvents(int page, String search, EventStatus eventStatus, int pageSize) {
        LOG.debug("## EventsPageResponse getPageEvents(int page, String search, String userStatusStringKey, int pageSize) ");
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Event> eventPage;
        if(eventStatus == null) eventStatus = EventStatus.UNDEFINED;

        if(StringUtils.hasText(search) && !Objects.equals(eventStatus, EventStatus.UNDEFINED)) {
            eventPage = this.eventRepository.findByTitleContainingIgnoreCaseAndStatus(
                    search, eventStatus, pageable);
        }
        else if(StringUtils.hasText(search)) {
            eventPage = this.eventRepository.findByTitleContainingIgnoreCase(search, pageable);
        }
        else if(!Objects.equals(eventStatus, EventStatus.UNDEFINED)) {
            eventPage = this.eventRepository.findByStatus(eventStatus, pageable);
        }
        else {
            eventPage = this.eventRepository.findAll(pageable);
        }

        EventsPageResponse response = new EventsPageResponse();
        response.setTotalPages(eventPage.getTotalPages());
        response.setEvents(EventMapper.toDTOList(eventPage.getContent()));
        response.setOk(true);
        return response;
    }

    public EventDetail getEventDetail(String eventUuid, User participant) {
        LOG.debug("## getEventDetail(String eventUuid)");
        Event event = this.getWithParticipantsByUuid(eventUuid);
        if(event == null) return null;

        EventDetail eventDetail = new EventDetail();
        eventDetail.setEvent(EventMapper.toDTO(event));

        for(EventParticipant ep : event.getParticipants()) {
            User user = ep.getParticipant();
            if(Objects.equals(user.getId(), participant.getId())){
                eventDetail.setRegistered(
                        Objects.equals(EventParticipantStatus.APPROVED, ep.getStatus()));
                break;
            }
        }

        int nbParticipant = (int) event.getParticipants()
                .stream()
                .filter(ep -> Objects.equals(EventParticipantStatus.APPROVED, ep.getStatus()))
                .count();
        eventDetail.setNbParticipants(nbParticipant);

        return eventDetail;
    }
}
