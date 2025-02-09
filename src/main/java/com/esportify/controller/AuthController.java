package com.esportify.controller;

import com.esportify.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;

@Controller
public class AuthController extends BaseController{

    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    @GetMapping("/auth")
    public String authPage(Model model, @AuthenticationPrincipal User user) {
        LOG.debug("## authPage(Model model)");
        this.addAttributes(model, user);
        model.addAttribute("page", "auth");
        return "auth";
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        LOG.debug("## logout(HttpServletRequest request)");
        HttpSession session = request.getSession(false);
        if(session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Collections.singletonMap("message", "Déconnexion réussie"));
    }

}
