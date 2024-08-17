package ru.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventSearch;

public interface EventFindRepository {

    Page<Event> findAllByParam(EventSearch eventSearch, Pageable pageable);
}
