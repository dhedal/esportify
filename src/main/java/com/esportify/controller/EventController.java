package com.esportify.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EventController extends BaseController{
    private static final Logger LOG = LoggerFactory.getLogger(EventController.class);

    @GetMapping("/events")
    public String eventsPage(Model model) {
        LOG.debug("## String eventList(Model model)");
        this.addAuthAttribute(model);
        model.addAttribute("page", "events");
        return "events";
    }
}
