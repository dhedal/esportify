package com.esportify.repository;

import com.esportify.entity.Event;
import com.esportify.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    public List<Event> findAllByOrganizer(User organizer);
    public Event findByUuid(String uuid);
    public List<Event> findByTitle(String title);
    @EntityGraph(attributePaths = {"participants"})
    Event findWithParticipantsByUuid(String uuid);
}
