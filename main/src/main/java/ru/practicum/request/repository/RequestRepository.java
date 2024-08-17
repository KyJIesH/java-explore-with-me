package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.request.model.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByRequesterId(Long userId);

    List<Request> findAllRequestsByRequesterIdAndEventId(Long userId, Long eventId);

    List<Request> findAllByEventId(Long eventId);

    List<Request> findByIdIn(List<Long> ids);
}
