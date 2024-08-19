package ru.practicum.event.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventAdminFindParam;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.model.State;
import ru.practicum.event.service.EventService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@Validated
@Slf4j
@AllArgsConstructor
@RequestMapping("/admin/events")
public class EventAdminController {
    private static final String TAG = "EventAdmin CONTROLLER";
    private final EventService eventService;

    @GetMapping()
    public ResponseEntity<List<EventFullDto>> findAdminAllEvents(@RequestParam(required = false) List<Long> users,
                                                                 @RequestParam(required = false) List<String> states,
                                                                 @RequestParam(required = false) List<Long> categories,
                                                                 @RequestParam(required = false)
                                                                 @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                                 @RequestParam(required = false)
                                                                 @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                                 @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                                 @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("{} - Пришел запрос на получение полной информации обо всех событиях подходящих под переданные " +
                "условия (GET /admin/events)", TAG);

        List<State> statesList = null;
        if (states != null) {
            statesList = states.stream().map(State::from).filter(Objects::nonNull).collect(Collectors.toList());
        }

        EventAdminFindParam param = new EventAdminFindParam();
        param.setUsers(users);
        param.setStates(statesList);
        param.setCategories(categories);
        param.setRangeStart(rangeStart);
        param.setRangeEnd(rangeEnd);
        param.setFrom(from);
        param.setSize(size);

        List<EventFullDto> response = eventService.findAdminAllEvents(param);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateStatusEventAdmin(@PathVariable Long eventId,
                                                               @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("{} - Пришел запрос на редактирование данных события и его статуса (отклонение/публикация) " +
                "(PATCH /admin/events/{eventId})", TAG);

        EventFullDto response = eventService.updateStatusEventAdmin(eventId, updateEventAdminRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
