package com.esportify.controller;

import com.esportify.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EventController extends BaseController{
    private static final Logger LOG = LoggerFactory.getLogger(EventController.class);

    @GetMapping("/events")
    public String eventsPage(Model model, @AuthenticationPrincipal User user) {
        LOG.debug("## String eventsPage(Model model)");
        this.addAttributes(model, user);
        model.addAttribute("page", "events");
        return "events";
    }
}
