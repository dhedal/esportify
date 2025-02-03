package com.esportify.repository;

import com.esportify.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public Optional<User> findByUuid(String uuid);
    public Optional<User> findByEmail(String email);
    public Optional<User> findByPseudo(String pseudo);
}
