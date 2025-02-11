package com.esportify.rest;

import com.esportify.dto.UserDTO;
import com.esportify.entity.User;
import com.esportify.enumerations.UserStatus;
import com.esportify.service.AuthenticationService;
import com.esportify.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController  extends BaseRestController{
    private static final Logger LOG = LoggerFactory.getLogger(AdminRestController.class);

    private UserService userService;

    @Autowired
    public AdminRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers(@AuthenticationPrincipal User admin) {
        LOG.debug("## getAllUsers(@AuthenticationPrincipal User admin)");
        if (admin == null || !admin.getStatus().equals(UserStatus.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.emptyList());
        }
        return ResponseEntity.ok(this.userService.getAllUsers());
    }
}
