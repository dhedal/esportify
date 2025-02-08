package com.esportify.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController extends BaseController{
    private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);

    @GetMapping("/account")
    public String accountPage(Model model) {
        LOG.debug("## accountPage(Model model)");
        this.addAuthAttribute(model);
        model.addAttribute("page", "account");
        return "account";
    }
}
