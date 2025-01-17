package com.esportify.service;

import com.esportify.dto.Response;
import com.esportify.dto.RegisterRequest;
import com.esportify.dto.RegisterResponse;
import com.esportify.entity.User;
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
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(this.passwordEncoder.encode(request.getPassword()));
        user = this.userService.save(user);

        try {
            response.setUserDTO(UserMapper.toDTO(user));
            response.setOk(true);
        } catch (Exception e) {
            LOG.error("Erreur lors de la conversion de l'utilisateur en DTO", e);
            response.addMessage("Désolé, une erreur interne est survenue.");
        }

        return response;

    }
}
