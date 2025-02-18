package com.esportify.controller;

import com.esportify.dto.EventDetail;
import com.esportify.entity.Event;
import com.esportify.entity.User;
import com.esportify.service.EventParticipantService;
import com.esportify.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class EventController extends BaseController{
    private static final Logger LOG = LoggerFactory.getLogger(EventController.class);
    private final EventService eventService;
    private EventParticipantService eventParticipantService;

    @Autowired
    public EventController(
            EventService eventService,
            EventParticipantService eventParticipantService) {
        this.eventService = eventService;
        this.eventParticipantService = eventParticipantService;
    }

    @GetMapping("/events")
    public String eventsPage(Model model, @AuthenticationPrincipal User user) {
        LOG.debug("## String eventsPage(Model model)");
        this.addAttributes(model, user);
        model.addAttribute("page", "events");
        return "events";
    }

    @GetMapping("/events/detail/{uuid}")
    public String eventDetailPage(@PathVariable String uuid, Model model, @AuthenticationPrincipal User user) {
        LOG.debug("## eventDetailPage(String uuid, Model model)");

        EventDetail eventDetail = this.eventService.getEventDetail(uuid, user);
        if (eventDetail == null) {
            LOG.warn("Événement non trouvé pour EVENT-UUID: {}", uuid);
            return "redirect:/events"; // Redirection vers la liste des événements si l'UUID est invalide
        }


        this.addAttributes(model, user);
        model.addAttribute("page", "events-detail");
        model.addAttribute("eventDetail", eventDetail);
        return "event-detail";
    }


}
