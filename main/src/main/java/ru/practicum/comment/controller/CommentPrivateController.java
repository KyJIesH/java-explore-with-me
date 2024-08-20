package ru.practicum.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentCreatedDto;
import ru.practicum.comment.dto.CommentUpdatedDto;
import ru.practicum.comment.dto.FullCommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.service.CommentService;

import java.util.List;

@Controller
@Validated
@Slf4j
@AllArgsConstructor
@RequestMapping("/users/{userId}/comments")
public class CommentPrivateController {
    private static final String TAG = "CommentPrivate CONTROLLER";
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentCreatedDto> createComment(@PathVariable Long userId,
                                                           @RequestParam Long eventId,
                                                           @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("{} - Пришел запрос на добавление комментария (POST /users/{userId}/comments)", TAG);
        CommentCreatedDto response = commentService.createComment(userId, eventId, newCommentDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<FullCommentDto> findCommentByUserIdAndCommentId(@PathVariable Long userId,
                                                                          @PathVariable Long commentId) {
        log.info("{} - Пришел запрос на получение комментария по id {}" +
                "(GET /users/{userId}/comments/{commentId})", TAG, commentId);
        FullCommentDto response = commentService.findCommentByUserIdAndCommentId(userId, commentId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<FullCommentDto>> findAllCommentsByUserId(@PathVariable Long userId,
                                                                        @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                                        @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("{} - Пришел запрос на получение всех комментариев пользователя с id {}" +
                "(GET /users/{userId}/comments)", TAG, userId);
        List<FullCommentDto> response = commentService.findAllCommentsByUserId(userId, from, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<FullCommentDto> updateComment(@PathVariable Long userId,
                                                        @PathVariable Long commentId,
                                                        @Valid @RequestBody CommentUpdatedDto commentUpdatedDto) {
        log.info("{} - Пришел запрос на обновление комментария с id {}" +
                "(PATCH /users/{userId}/comments/{commentId})", TAG, commentId);
        FullCommentDto response = commentService.updateComment(userId, commentId, commentUpdatedDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long userId,
                                                @PathVariable Long commentId) {
        log.info("{} - Пришел запрос на удаление комментария с id {}" +
                "(DELETE /users/{userId}/comments/{commentId})", TAG, commentId);
        commentService.deleteComment(userId, commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}