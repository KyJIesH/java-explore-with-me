package ru.practicum.comment.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.comment.dto.FullCommentDto;
import ru.practicum.comment.service.CommentService;

import java.util.List;

@Controller
@Validated
@Slf4j
@AllArgsConstructor
@RequestMapping("/comments")
public class CommentPublicController {
    private static final String TAG = "CommentPublic CONTROLLER";
    private final CommentService commentService;

    @GetMapping("/comment/{commentId}")
    public ResponseEntity<FullCommentDto> findCommentById(@PathVariable Long commentId) {
        log.info("{} - Пришел запрос на получение комментария по id {} (GET /comments/comment/{commentId})", TAG, commentId);
        FullCommentDto response = commentService.findCommentById(commentId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<FullCommentDto>> findCommentsByEventId(@PathVariable Long eventId) {
        log.info("{} - Пришел запрос на получение всех комментариев события с id {} (GET /comments/event/{eventId})", TAG, eventId);
        List<FullCommentDto> response = commentService.findCommentsByEventId(eventId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<FullCommentDto>> findCommentsByText(@RequestParam String text,
                                                                   @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                                   @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("{} - Пришел запрос на поиск комментариев по тексту (GET /comments)", TAG);
        Pageable pageable = PageRequest.of(from / size, size);
        List<FullCommentDto> response = commentService.findCommentsByText(text, pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
