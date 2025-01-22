package com.esportify.repository;

import com.esportify.entity.Event;
import com.esportify.entity.EventParticipant;
import com.esportify.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {
    public EventParticipant findByUuid(String uuid);
    public List<EventParticipant> findAllByEvent(Event event);
    @EntityGraph(attributePaths = {"event", "participant"})
    EventParticipant findByEventAndParticipant(Event event, User participant);

}
