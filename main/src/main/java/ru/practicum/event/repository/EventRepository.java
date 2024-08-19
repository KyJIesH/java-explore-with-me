package ru.practicum.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, EventFindRepository {

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long initiatorId);

    List<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    List<Event> findAllByCategoryId(Long catId);

    @Query("SELECT e FROM Event e " +
            "WHERE (e.initiator.id IN (:users) OR :users IS NULL) " +
            "AND (e.state IN (:states) OR :states IS NULL) " +
            "AND (e.category.id IN (:categories) OR :categories IS NULL) "
    )
    List<Event> findAdminAllEventsWithoutRange(
            @Param("users") List<Long> users,
            @Param("states") List<State> states,
            @Param("categories") List<Long> categories,
            Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE (e.initiator.id IN (:users) OR :users IS NULL) " +
            "AND (e.state IN (:states) OR :states IS NULL) " +
            "AND (e.category.id IN (:categories) OR :categories IS NULL) " +
            "AND (e.eventDate > :rangeStart ) AND (e.eventDate < :rangeEnd) " +
            "AND (e.eventDate " +
            "BETWEEN :rangeStart AND :rangeEnd)"
    )
    List<Event> findAdminAllEvents(
            @Param("users") List<Long> users,
            @Param("states") List<State> states,
            @Param("categories") List<Long> categories,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable);

    List<Event> findAllByIdIn(Collection<Long> eventIds);
}
