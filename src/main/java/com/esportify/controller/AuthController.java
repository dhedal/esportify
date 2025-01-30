package com.esportify.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    @GetMapping("/auth")
    public String authPage(Model model) {
        LOG.debug("## authPage(Model model)");
        return "auth";
    }

}
