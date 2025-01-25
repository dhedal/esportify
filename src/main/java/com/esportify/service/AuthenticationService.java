package com.esportify.service;

import com.esportify.dto.*;
import com.esportify.entity.User;
import com.esportify.enumerations.UserStatus;
import com.esportify.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Objects;
import java.util.Set;

@Service
public class AuthenticationService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationService.class);

    private Validator validator;
    private BCryptPasswordEncoder passwordEncoder;
    private UserService userService;

    @Autowired
    public AuthenticationService(
            UserService userService,
            Validator validator,
            BCryptPasswordEncoder passwordEncoder

    ) {
        this.userService = userService;
        this.validator = validator;
        this.passwordEncoder = passwordEncoder;
    }

    public RegisterResponse register(RegisterRequest request, RegisterResponse response) {
        LOG.debug("## register(RegisterRequest request, RegisterResponse response)");

        if(Objects.isNull(request)){
            throw new IllegalArgumentException("RegisterRequest ne doit pas être null");
        }
        if(Objects.isNull(response)){
            throw new IllegalArgumentException("RegisterResponse ne doit pas être null");
        }

        Set<ConstraintViolation<RegisterRequest>> violations = this.validator.validate(request);
        if(!violations.isEmpty()) {
            for(ConstraintViolation<RegisterRequest> violation : violations) {
                response.addMessage(violation.getMessage());
            }
            return response;
        }

        if(this.userService.isEmailExist(request.getEmail())) {
            response.addMessage("L'email existe déjà.");
            return response;
        }

        User user = new User();
        user.setPseudo(request.getPseudo());
        user.setEmail(request.getEmail());
        user.setPassword(this.passwordEncoder.encode(request.getPassword()));
        user.setStatus(UserStatus.PLAYER);
        user = this.userService.save(user);

        try {
            response.setUserDTO(UserMapper.toDTO(user));
            response.setOk(true);
        } catch (Exception e) {
            LOG.error("Erreur lors de la conversion de l'utilisateur en DTO", e);
            response.addMessage("Désolé, une erreur interne est survenue.");
            response.setOk(false);
            response.setUserDTO(null);
        }

        return response;
    }

    public LoginResponse login(LoginRequest request, LoginResponse response) {
        LOG.debug("## login(LoginRequest request, LoginResponse response)");

        if(Objects.isNull(request)){
            throw new IllegalArgumentException("LoginRequest ne doit pas être null");
        }
        if(Objects.isNull(response)){
            throw new IllegalArgumentException("LoginResponse ne doit pas être null");
        }

        Set<ConstraintViolation<LoginRequest>> violations = this.validator.validate(request);
        if(!violations.isEmpty()) {
            for(ConstraintViolation<LoginRequest> violation : violations) {
                response.addMessage(violation.getMessage());
            }
            return response;
        }

        User user = this.userService.getByEmail(request.getEmail());
        if(Objects.isNull(user)) {
            response.addMessage("L'email n'existe pas.");
            return response;
        }

        if(!this.passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            response.addMessage("Le mot de passe ne correspond pas.");
            return response;
        }

        try {
            response.setUserDTO(UserMapper.toDTO(user));
            response.setOk(true);
        } catch (Exception e) {
            LOG.error("Erreur lors de la conversion de l'utilisateur en DTO", e);
            response.addMessage("Désolé, une erreur interne est survenue.");
            response.setOk(false);
            response.setUserDTO(null);
        }

        return response;

    }

}
