package ru.practicum.event.service;

import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.request.dto.ParticipationRequestDto;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {

    Event findEventById(Long eventId);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    EventRequestStatusUpdateResult updateStatusEvent(Long userId, Long eventId,
                                                     EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    EventFullDto findEventByUserIdAndEventId(Long userId, Long eventId);

    List<EventShortDto> findAllEventsByUserId(Long userId, int from, int size);

    List<ParticipationRequestDto> findParticipationByUserIdAndEventId(Long userId, Long eventId);

    List<EventFullDto> findAdminAllEvents(EventAdminFindParam eventAdminFindParam);

    EventFullDto updateStatusEventAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventShortDto> findEventsParam(EventUserFindParam eventUserFindParam, HttpServletRequest request);

    EventFullDto findEventPublishedById(Long eventId, HttpServletRequest request);
}
