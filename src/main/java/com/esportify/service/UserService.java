package com.esportify.service;


import com.esportify.dto.UserDTO;
import com.esportify.dto.UsersPageResponse;

import com.esportify.entity.User;
import com.esportify.enumerations.UserStatus;
import com.esportify.mapper.UserMapper;
import com.esportify.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Service
public class UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User save(User user) {
        LOG.debug("## save(User user)");
        if(Objects.isNull(user)) throw new IllegalArgumentException("user ne doit pas être null");
        return this.userRepository.save(user);
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

    public boolean isEmailExist(String email) {
        LOG.debug("## isEmailExist(String email)");
        if(!StringUtils.hasText(email)) return false;
        User user = this.userRepository.findByEmail(email).orElse(null);
        return user != null;

    }

    public boolean isPseudoExist(String pseudo) {
        LOG.debug("## isPseudoExist(String pseudo)");
        User user = this.userRepository.findByPseudo(pseudo).orElse(null);
        return user != null;
    }

    public List<UserDTO> getAllUsers() {
        LOG.debug("## getAllUsers()");
        List<User> users = this.userRepository.findAll();
        return UserMapper.toDtoList(users);
    }

    /**
     *
     * @param page
     * @param search
     * @param userStatus
     * @param pageSize
     * @return
     */
    public UsersPageResponse getPageUsers(int page, String search, UserStatus userStatus, int pageSize) {
        LOG.debug("## UsersPageResponse getPageUsers(int page, String search, String userStatusStringKey, int pageSize) ");
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<User> userPage;
        if(userStatus == null) userStatus = UserStatus.UNDEFINED;

        if(StringUtils.hasText(search) && !Objects.equals(userStatus, UserStatus.UNDEFINED)) {
            userPage = this.userRepository.findByPseudoContainingIgnoreCaseOrEmailContainingIgnoreCaseAndStatus(
                    search, search, userStatus, pageable);
        }
        else if(StringUtils.hasText(search)) {
            userPage = this.userRepository.findByPseudoContainingIgnoreCaseOrEmailContainingIgnoreCase(
                    search, search, pageable);
        }
        else if(!Objects.equals(userStatus, UserStatus.UNDEFINED)) {
            userPage = this.userRepository.findByStatus(userStatus, pageable);
        }
        else {
            userPage = this.userRepository.findAll(pageable);
        }

        UsersPageResponse response = new UsersPageResponse();
        response.setTotalPages(userPage.getTotalPages());
        response.setUsers(UserMapper.toDtoList(userPage.getContent()));
        response.setOk(true);
        return response;
    }

    /**
     *
     * @param user
     * @param userStatus
     * @return
     */
    public boolean changeStatus(User user, UserStatus userStatus) {
        LOG.debug("## boolean changeStatus(User user, UserStatus userStatus)");
        if(user == null || user.isNew()) return false;
        if(userStatus == null) return false;

        user.setStatus(userStatus);
        this.userRepository.save(user);
        return true;
    }
}
