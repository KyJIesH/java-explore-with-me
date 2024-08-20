package ru.practicum.event.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.*;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.ParticipationRequestDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

@Controller
@Validated
@Slf4j
@AllArgsConstructor
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {
    private static final String TAG = "EventPrivate CONTROLLER";
    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventFullDto> createEvent(@PathVariable Long userId,
                                                    @Valid @RequestBody NewEventDto newEventDto) {
        log.info("{} - Пришел запрос на добавление события (POST /users/{userId}/events)", TAG);
        EventFullDto response = eventService.createEvent(userId, newEventDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(@PathVariable Long userId,
                                                    @PathVariable Long eventId,
                                                    @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("{} - Пришел запрос на обновление события (PATCH /users/{userId}/events/{eventId})", TAG);
        EventFullDto response = eventService.updateEvent(userId, eventId, updateEventUserRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping(value = {"/{eventId}/requests", "/{eventId}/requests/"})
    public ResponseEntity<EventRequestStatusUpdateResult> updateStatusEvent(@PathVariable Long userId,
                                                                            @PathVariable Long eventId,
                                                                            @RequestBody EventRequestStatusUpdateRequest
                                                                                 eventRequestStatusUpdateRequest) {
        log.info("{} - Пришел запрос на изменение статуса (подтверждена, отменена) заявок на участие в событии " +
                "текущего пользователя (PATCH /users/{userId}/events/{eventId}/requests)", TAG);
        EventRequestStatusUpdateResult response = eventService.updateStatusEvent(userId, eventId, eventRequestStatusUpdateRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> findEventByUserIdAndEventId(@PathVariable Long userId,
                                                                    @PathVariable Long eventId) {
        log.info("{} - Пришел запрос на получение события (GET /users/{userId}/events/{eventId})", TAG);
        EventFullDto response = eventService.findEventByUserIdAndEventId(userId, eventId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<EventShortDto>> findAllEventsByUserId(@PathVariable Long userId,
                                                                     @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                                     @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("{} - Пришел запрос на получение всех событиий (GET /users/{userId}/events)", TAG);
        List<EventShortDto> response = eventService.findAllEventsByUserId(userId, from, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> findParticipationByUserIdAndEventId(@PathVariable Long userId,
                                                                                             @PathVariable Long eventId) {
        log.info("{} - Пришел запрос на получение информации о запросах на участие " +
                "(GET /users/{userId}/events/{eventId}/requests)", TAG);
        List<ParticipationRequestDto> response = eventService.findParticipationByUserIdAndEventId(userId, eventId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
