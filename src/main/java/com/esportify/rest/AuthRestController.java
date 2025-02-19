package com.esportify.rest;

import com.esportify.dto.*;
import com.esportify.entity.User;
import com.esportify.mapper.UserMapper;
import com.esportify.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.lang.model.element.ModuleElement;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController extends BaseRestController{
    private static final Logger LOG = LoggerFactory.getLogger(AuthRestController.class);
    private AuthenticationService authenticationService;

    @Autowired
    public AuthRestController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
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

    @GetMapping("/me")
    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal User user) {
        LOG.debug("## getUserProfile(@AuthenticationPrincipal User user)");
        try {
            if (user != null) return ResponseEntity.ok(UserMapper.toDTO(user));
        }catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Collections.singletonMap("message", "Utilisateur non authentifié"));


    }

    @PutMapping(value = "/password", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> changePassword(@RequestBody PasswordRequest request, @AuthenticationPrincipal User user) {
        LOG.debug("## changePassword(@RequestBody RegisterRequest request, @AuthenticationPrincipal User user)");
        Response response = new Response();
        try {
            if (user == null) {
                ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(response);
            }
            response = this.authenticationService.changePassword(request, response, user);
            return ResponseEntity.ok(response);

        }catch (Exception e) {
            LOG.error(e.getMessage());
            response.setOk(false);
            response.addMessage("Désolé, une erreur interne est survenue.");
            return ResponseEntity.badRequest().body(response);
        }


    }

}
