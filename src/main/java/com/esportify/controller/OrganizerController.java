package com.esportify.controller;

import com.esportify.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class OrganizerController extends BaseController{
    private static final Logger LOG = LoggerFactory.getLogger(OrganizerController.class);

    @GetMapping("/organizer")
    public String organizerPage(Model model, @AuthenticationPrincipal User user) {
        LOG.debug("## organizerPage(Model model, @AuthenticationPrincipal User user)");
        this.addAttributes(model, user);
        model.addAttribute("page", "organizer");

        return "organizer";
    }
}
