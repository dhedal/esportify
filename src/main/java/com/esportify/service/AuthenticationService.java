package com.esportify.service;

import com.esportify.dto.*;
import com.esportify.entity.User;
import com.esportify.enumerations.UserStatus;
import com.esportify.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationService(
            UserService userService,
            AuthenticationManager authenticationManager,
            Validator validator,
            BCryptPasswordEncoder passwordEncoder

    ) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
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

        if(this.userService.isPseudoExist(request.getPseudo())) {
            response.addMessage("Le pseudo existe déjà.");
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

    public LoginResponse authenticate(LoginRequest request, LoginResponse response) {
        LOG.debug("## authenticate(LoginRequest request, LoginResponse response)");

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

        try {
            Authentication authentication = this.authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                User user = userService.getByEmail(userDetails.getUsername());

                response.setUserDTO(UserMapper.toDTO(user));
                response.setOk(true);
                return response;
            } else {
                LOG.error("L'objet principal retourné n'est pas un UserDetails.");
                response.addMessage("Erreur interne : type d'utilisateur inconnu.");
            }

        } catch (BadCredentialsException e) {
            LOG.error("Authentification échouée : identifiants invalides");
            response.addMessage("L'email ou le mot de passe est incorrect.");
        } catch (Exception e) {
            LOG.error("Erreur interne lors de l'authentification", e);
            response.addMessage("Désolé, une erreur interne est survenue.");
        }

        return response;

    }

}
