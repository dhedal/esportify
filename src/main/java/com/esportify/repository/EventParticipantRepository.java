package com.esportify.repository;

import com.esportify.entity.Event;
import com.esportify.entity.EventParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {
    public EventParticipant findByUuid(String uuid);
    public List<EventParticipant> findAllByEvent(Event event);
}
