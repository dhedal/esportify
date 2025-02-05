package com.esportify.rest;

import com.esportify.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class BaseRestController {

//    public User getAuthenticatedUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if(authentication != null && authentication.isAuthenticated()
//                && authentication.getPrincipal() instanceof User) {
//            return (User)authentication.getPrincipal();
//        }
//        return null;
//
//    }
}
