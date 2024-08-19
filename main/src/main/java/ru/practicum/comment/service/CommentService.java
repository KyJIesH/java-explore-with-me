package ru.practicum.comment.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.comment.dto.*;

import java.util.List;

public interface CommentService {
    CommentCreatedDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    FullCommentDto findCommentByUserIdAndCommentId(Long userId, Long commentId);

    List<FullCommentDto> findAllCommentsByUserId(Long userId, int from, int size);

    FullCommentDto updateComment(Long userId, Long commentId, CommentUpdatedDto newCommentDto);

    void deleteComment(Long userId, Long commentId);

    FullCommentDto findCommentById(Long commentId);

    List<FullCommentDto> findCommentsByEventId(Long eventId);

    List<FullCommentDto> findCommentsByText(String text, Pageable pageable);
}
