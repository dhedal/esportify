package com.esportify.controller;

import com.esportify.dto.EventDTO;
import com.esportify.dto.LoginRequest;
import com.esportify.dto.LoginResponse;
import com.esportify.entity.User;
import com.esportify.service.AuthenticationService;
import com.esportify.service.EventService;
import com.esportify.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController extends BaseController{
    private static final Logger LOG = LoggerFactory.getLogger(HomeController.class);

    private EventService eventService;
    private AuthenticationService authenticationService;
    private UserService userService;
    @Autowired
    public HomeController(
            EventService eventService,
            AuthenticationService authenticationService,
            UserService userService) {
        this.eventService = eventService;
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    @GetMapping("/home")
    public String home(Model model, @AuthenticationPrincipal User user) {
        this.autoConnect(user);
        LOG.debug("## home(Model model)");
        this.addAttributes(model, user);
        List<EventDTO> events = this.eventService.getUpcomingAndOngoingEvents();
        model.addAttribute("events", events);
        model.addAttribute("title", "Accueil");
        return "home";
    }

    public User autoConnect(User user) {
        if(user != null) return user;
        try {

            // 🔥 Créer une requête de connexion
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail("admin@esportify.com");
            loginRequest.setPassword("StrongPassword!1");

            // 🔥 Exécuter l'authentification
            LoginResponse response = new LoginResponse();
            authenticationService.authenticate(loginRequest, response);

            if (response.isOk()) {
                System.out.println("Connexion automatique réussie pour admin@esportify.com !");
            } else {
                System.out.println("Échec de la connexion automatique : " + response.getMessages());
            }


        } catch (Exception e) {
            System.out.println("Impossible de connecter automatiquement l'utilisateur.");
            e.printStackTrace();
        }
        return user;
    }

}
