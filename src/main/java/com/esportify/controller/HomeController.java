package com.esportify.controller;

import com.esportify.dto.EventDTO;
import com.esportify.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    private static final Logger LOG = LoggerFactory.getLogger(HomeController.class);

    private EventService eventService;
    @Autowired
    public HomeController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/")
    public String home(Model model) {
        LOG.debug("## home(Model model)");
        List<EventDTO> events = this.eventService.getUpcomingAndOngoingEvents();
        model.addAttribute("events", events);
        model.addAttribute("title", "Accueil");
        return "home";
    }

}
