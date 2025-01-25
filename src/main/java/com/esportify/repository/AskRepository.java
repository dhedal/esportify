package com.esportify.repository;

import com.esportify.entity.Ask;
import com.esportify.enumerations.AskStatus;
import com.esportify.enumerations.AskType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AskRepository extends JpaRepository<Ask, Long> {
    @EntityGraph(attributePaths = {"author"})
    public List<Ask> findAllByTypeAndStatus(AskType type, AskStatus status);

    @EntityGraph(attributePaths = {"author"})
    public Ask findByUuid(String uuid);
}
