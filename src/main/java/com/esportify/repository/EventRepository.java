package com.esportify.repository;

import com.esportify.entity.Event;
import com.esportify.entity.User;
import com.esportify.enumerations.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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

    @EntityGraph(attributePaths = {"organizer"})
    List<Event> findByOrganizer(User organizer);

    /**
     * Recherche par titre et par status avec pagination
     * @param title
     * @param status
     * @param pageable
     * @return
     */
    public Page<Event> findByTitleContainingIgnoreCaseAndStatus(String title, EventStatus status, Pageable pageable);

    /**
     * Recherche par title avec pagination
     * @param title
     * @param pageable
     * @return
     */
    public Page<Event> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    /**
     * Recherche par status avec pagination
     * @param status
     * @param pageable
     * @return
     */
    public Page<Event> findByStatus(EventStatus status, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE "
            + "e.status IN (:validStatuses) " // Filtre sur les statuts acceptés
            + "AND (:search IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%'))) "
            + "AND (:players IS NULL OR e.maxPlayers <= :players) "
            + "AND (:date IS NULL OR FUNCTION('DATE', e.startDateTime) = :date) " // Conversion de LocalDateTime en DATE
            + "AND (:organizer IS NULL OR LOWER(e.organizer.pseudo) LIKE LOWER(CONCAT('%', :organizer, '%')))")
    public Page<Event> findFilteredEvents(
            @Param("validStatuses") List<EventStatus> validStatuses,
            @Param("search") String search,
            @Param("players") Integer players,
            @Param("date") LocalDate date,
            @Param("organizer") String organizer,
            Pageable pageable
    );

    @Query("SELECT DISTINCT e.organizer FROM Event e WHERE e.status IN (:validStatuses)")
    public List<User> findDistinctOrganizers(@Param("validStatuses") List<EventStatus> validStatuses);

}
