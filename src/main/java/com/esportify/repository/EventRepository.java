package com.esportify.repository;

import com.esportify.entity.Event;
import com.esportify.entity.User;
import com.esportify.enumerations.EventStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    public List<Event> findAllByOrganizer(User organizer);
    public Event findByUuid(String uuid);
    public List<Event> findByTitle(String title);
    @EntityGraph(attributePaths = {"participants"})
    Event findWithParticipantsByUuid(String uuid);

    @Query("SELECT e FROM Event e WHERE e.status IN (:statuses) AND e.endDateTime >= CURRENT_TIMESTAMP ORDER BY e.startDateTime ASC")
    List<Event> findUpcomingAndOngoingEvents(@Param("statuses") List<EventStatus> statuses);

}
