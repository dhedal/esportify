package com.esportify.service;

import com.esportify.entity.User;
import com.esportify.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
public class UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getByEmail(String email) {
        LOG.debug("## getByEmail(String email)");
        if(!StringUtils.hasText(email)) return null;
        return this.userRepository.findByEmail(email).orElse(null);
    }

    public User getByUuid(String uuid) {
        CollectionUtils.arrayToList(null);
        LOG.debug("## getByUuid(String email)");
        if(!StringUtils.hasText(uuid)) return null;
        return this.userRepository.findByUuid(uuid).orElse(null);
    }
}
