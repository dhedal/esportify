package com.esportify.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

public abstract class BaseController {

    protected void addAuthAttribute(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String); // Vérifie que ce n'est pas "anonymousUser"
        model.addAttribute("isAuthenticated", isAuthenticated);
    }

}
