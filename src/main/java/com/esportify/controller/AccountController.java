package com.esportify.controller;

import com.esportify.entity.User;
import com.esportify.enumerations.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Objects;

@Controller
public class AccountController extends BaseController{
    private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);

    @GetMapping("/account")
    public String accountPage(Model model, @AuthenticationPrincipal User user) {
        LOG.debug("## accountPage(Model model)");
        this.addAttributes(model, user);
        model.addAttribute("page", "account");

        return "account";
    }
}
