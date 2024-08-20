package ru.practicum.event.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventUserFindParam;
import ru.practicum.event.service.EventService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@Validated
@Slf4j
@AllArgsConstructor
@RequestMapping("/events")
public class EventPublicController {
    private static final String TAG = "EventPublic CONTROLLER";
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> findEventsFilter(@RequestParam(required = false) String text,
                                                                @RequestParam(required = false) List<Long> categories,
                                                                @RequestParam(required = false) Boolean paid,
                                                                @RequestParam(required = false)
                                                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                                @RequestParam(required = false)
                                                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                                @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                                @RequestParam(defaultValue = "EVENT_DATE") String sort,
                                                                @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                                @Positive @RequestParam(defaultValue = "10") int size,
                                                                HttpServletRequest request) {
        log.info("{} - Пришел запрос на получение событий с возможностью фильтрации (GET /events)", TAG);

        EventUserFindParam eventUserFindParam = new EventUserFindParam();
        eventUserFindParam.setText(text);
        eventUserFindParam.setCategories(categories);
        eventUserFindParam.setPaid(paid);
        eventUserFindParam.setRangeStart(rangeStart);
        eventUserFindParam.setRangeEnd(rangeEnd);
        eventUserFindParam.setOnlyAvailable(onlyAvailable);
        eventUserFindParam.setSort(sort);
        eventUserFindParam.setFrom(from);
        eventUserFindParam.setSize(size);

        List<EventShortDto> response = eventService.findEventsParam(eventUserFindParam, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> findEventPublishedById(@PathVariable Long id,
                                                               HttpServletRequest request) {
        log.info("{} - Пришел запрос на получение подробной информации об опубликованном событии по id {} " +
                "(GET /events/{id})", TAG, id);
        EventFullDto response = eventService.findEventPublishedById(id, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
