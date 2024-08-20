package ru.practicum.comment.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentCreatedDto;
import ru.practicum.comment.dto.CommentUpdatedDto;
import ru.practicum.comment.dto.FullCommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private static final String TAG = "COMMENT SERVICE";
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;

    //Закрытый API для работы с комментариями
    @Override
    @Transactional
    public CommentCreatedDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        log.info("{} - Обработка запроса на добавление комментария", TAG);
        User user = checkUserById(userId);
        Event event = checkEventById(eventId);

        Comment comment = new Comment();

        if (event.getState().equals(State.PUBLISHED) || event.getState().equals(State.CANCELED)) {
            comment.setText(newCommentDto.getText());
            comment.setAuthor(user);
            comment.setEvent(event);
            comment.setCreated(LocalDateTime.now());
            comment.setUpdated(LocalDateTime.now());

            commentRepository.save(comment);
        } else {
            throw new ConflictException("Нельзя создать комментарий о неопубликованном или отмененном событии");
        }
        return commentMapper.toCommentCreatedDto(comment);
    }

    @Override
    public FullCommentDto findCommentByUserIdAndCommentId(Long userId, Long commentId) {
        log.info("{} - Обработка запроса на получение комментария по id {}", TAG, commentId);
        checkUserById(userId);
        Comment comment = checkCommentById(commentId);
        return commentMapper.toFullCommentDto(comment);
    }

    @Override
    public List<FullCommentDto> findAllCommentsByUserId(Long userId, int from, int size) {
        log.info("{} - Обработка запроса на получение всех комментариев пользователя с id {}", TAG, userId);
        List<Comment> response = new ArrayList<>();
        Pageable pageable = PageRequest.of(from / size, size);
        checkUserById(userId);
        response = commentRepository.findAllByAuthorId(userId, pageable);
        return commentMapper.toFullCommentDtoList(response);
    }

    @Override
    @Transactional
    public FullCommentDto updateComment(Long userId, Long commentId, CommentUpdatedDto commentUpdatedDto) {
        log.info("{} - Обработка запроса на обновление комментария с id {}", TAG, commentId);
        checkAuthor(userId, commentId);
        Comment comment = checkCommentById(commentId);
        comment.setText(commentUpdatedDto.getText());
        comment.setUpdated(LocalDateTime.now());
        return commentMapper.toFullCommentDto(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        log.info("{} - Обработка запроса на удаление комментария с id {}", TAG, commentId);
        checkAuthor(userId, commentId);
        Comment comment = checkCommentById(commentId);
        commentRepository.delete(comment);
    }

    //Public API для работы с комментариями
    @Override
    public FullCommentDto findCommentById(Long commentId) {
        log.info("{} - Обработка запроса на получение комментария по id {})", TAG, commentId);
        Comment comment = checkCommentById(commentId);
        return commentMapper.toFullCommentDto(comment);
    }

    @Override
    public List<FullCommentDto> findCommentsByEventId(Long eventId) {
        log.info("{} - Обработка запроса на получение всех комментариев события с id {})", TAG, eventId);
        checkEventById(eventId);
        List<Comment> comments = commentRepository.findAllByEventId(eventId);
        return commentMapper.toFullCommentDtoList(comments);
    }

    @Override
    public List<FullCommentDto> findCommentsByText(String text, Pageable pageable) {
        log.info("{} - Обработка запроса на поиск комментариев по тексту", TAG);
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        List<Comment> comments = commentRepository.search(text, pageable).toList();
        return commentMapper.toFullCommentDtoList(comments);
    }

    private User checkUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    private Event checkEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
    }

    private Comment checkCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));
    }

    private void checkAuthor(Long userId, Long commentId) {
        User user = checkUserById(userId);
        Comment comment = checkCommentById(commentId);
        if (!user.getId().equals(comment.getAuthor().getId())) {
            throw new ConflictException("Ошибка проверки авторства комментария");
        }
    }
}
