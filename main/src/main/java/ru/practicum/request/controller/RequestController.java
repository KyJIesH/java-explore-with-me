package ru.practicum.request.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import java.util.List;

@Controller
@Slf4j
@AllArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class RequestController {
    private static final String TAG = "Request CONTROLLER";
    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> createRequest(@PathVariable Long userId,
                                                                 @RequestParam Long eventId) {
        log.info("{} - Пришел запрос на добавление запроса (POST /users/{userId}/requests)", TAG);
        ParticipationRequestDto response = requestService.createRequest(userId, eventId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> findRequest(@PathVariable Long userId) {
        log.info("{} - Пришел запрос на получение запроса (GET /users/{userId}/requests)", TAG);
        List<ParticipationRequestDto> responses = requestService.findRequest(userId);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> updateRequest(@PathVariable Long userId,
                                                                 @PathVariable Long requestId) {
        log.info("{} - Пришел запрос на отмену запроса (PATCH /users/{userId}/requests/{requestId}/cancel)", TAG);
        ParticipationRequestDto response = requestService.updateRequest(userId, requestId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
