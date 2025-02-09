package com.esportify.controller;

import com.esportify.entity.User;
import com.esportify.enumerations.UserStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import java.util.Objects;

public abstract class BaseController {

    protected void addAttributes(Model model) {
        this.addAttributes(model, null);
    }

    protected void addAttributes(Model model, User user) {
        this.addAuthAttribute(model);
        this.addIsPlayerStatusAttribute(model, user);
        this.addIsAdminAttribute(model, user);
        this.addIsOrganizerAttribute(model, user);
    }

    protected void addAuthAttribute(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String); // Vérifie que ce n'est pas "anonymousUser"
        model.addAttribute("isAuthenticated", isAuthenticated);
    }

    protected void addIsPlayerStatusAttribute(Model model, User user) {
        boolean isPlayerStatus = user != null && Objects.equals(user.getStatus(), UserStatus.PLAYER);
        model.addAttribute("isPlayerStatus", isPlayerStatus);
    }

    protected void addIsAdminAttribute(Model model, User user) {
        boolean isAdmin = user != null && Objects.equals(user.getStatus(), UserStatus.ADMIN);
        model.addAttribute("isAdmin", isAdmin);
    }

    protected void addIsOrganizerAttribute(Model model, User user) {
        boolean isOrganiser = user != null &&
                (Objects.equals(user.getStatus(), UserStatus.ORGANIZER) || Objects.equals(user.getStatus(), UserStatus.ADMIN));
        model.addAttribute("isOrganizer", isOrganiser);
    }

}
