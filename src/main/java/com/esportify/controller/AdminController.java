package com.esportify.controller;

import com.esportify.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController  extends BaseController{
    private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);

    @GetMapping("/admin")
    public String adminPage(Model model, @AuthenticationPrincipal User user) {
        LOG.debug("## adminPage(Model model)");
        this.addAttributes(model, user);
        model.addAttribute("page", "admin");

        return "admin";
    }
}
