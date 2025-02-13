package com.esportify.repository;

import com.esportify.entity.User;
import com.esportify.enumerations.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public Optional<User> findByUuid(String uuid);
    public Optional<User> findByEmail(String email);
    public Optional<User> findByPseudo(String pseudo);

    // Recherche par pseudo ou email avec pagination
    public Page<User> findByPseudoContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String pseudo, String email, Pageable pageable);

    // Recherche par rôle avec pagination
    public Page<User> findByStatus(UserStatus status, Pageable pageable);

    // Recherche par pseudo ou email ET rôle avec pagination
    public Page<User> findByPseudoContainingIgnoreCaseOrEmailContainingIgnoreCaseAndStatus(
            String pseudo, String email, UserStatus status, Pageable pageable);

}
