package com.esportify.rest;

import com.esportify.dto.LoginRequest;
import com.esportify.dto.LoginResponse;
import com.esportify.dto.RegisterRequest;
import com.esportify.dto.RegisterResponse;
import com.esportify.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.lang.model.element.ModuleElement;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {
    private static final Logger LOG = LoggerFactory.getLogger(AuthRestController.class);
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthRestController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(value="/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginRequest request) {
        LOG.debug("## authenticate(@RequestBody LoginRequest request)");
        final LoginResponse response = new LoginResponse();
        try {
            this.authenticationService.authenticate(request, response);
        } catch (Exception ex) {
            LOG.error(ex.toString());
            response.addMessage("Un problème est survenu, veuillez réessayer ultérieurement");
            response.setOk(false);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        LOG.debug("## register(SignupRequest request)");
        final RegisterResponse response = new RegisterResponse();
        try {
            this.authenticationService.register(request, response);
        } catch (Exception e){
            LOG.error(e.toString());
            response.addMessage("Un problème est survenu, veuillez réessayer ultérieurement");
        }
        return ResponseEntity.ok(response);
    }

}
