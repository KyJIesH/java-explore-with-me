package ru.practicum.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.Status;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {

    private static final String TAG = "REQUEST SERVICE";
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        log.info("{} - Обработка запроса на добавление запроса", TAG);
        User user = checkUser(userId);
        Event event = checkEvent(eventId);

        List<Request> requests = requestRepository.findAllRequestsByRequesterIdAndEventId(userId, eventId);

        if (!requests.isEmpty()) {
            throw new ConflictException("Нельзя добавить повторный запрос");
        }
        if (userId.equals(event.getInitiator().getId())) {
            throw new ConflictException("Инициатор события не может добавить запрос на участие в своём событии");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии");
        }
        if (event.getParticipantLimit() != 0L && event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            throw new ConflictException("У события достигнут лимит запросов на участие");
        }

        Request request = new Request();
        request.setRequester(user);
        request.setEvent(event);
        request.setCreated(LocalDateTime.now());
        request.setStatus(Status.PENDING);

        if (event.getParticipantLimit() == 0L || !event.getRequestModeration()) {
            request.setStatus(Status.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1L);
            eventRepository.save(event);
        }

        Request response = requestRepository.save(request);
        return requestMapper.toParticipationRequestDto(response);
    }

    @Override
    public List<ParticipationRequestDto> findRequest(Long userId) {
        log.info("{} - Обработка запроса на получение запроса", TAG);
        checkUser(userId);
        List<Request> requests = requestRepository.findAllByRequesterId(userId);

        List<ParticipationRequestDto> response = new ArrayList<>();
        for (Request request : requests) {
            response.add(requestMapper.toParticipationRequestDto(request));
        }
        return response;
    }

    @Override
    @Transactional
    public ParticipationRequestDto updateRequest(Long userId, Long requestId) {
        log.info("{} - Обработка запроса на отмену запроса", TAG);
        checkUser(userId);
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));

        if (!userId.equals(request.getRequester().getId())) {
            throw new ConflictException("Запрос не принадлежит пользователю c id: " + userId);
        }
        request.setStatus(Status.CANCELED);
        requestRepository.save(request);
        return requestMapper.toParticipationRequestDto(request);
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    private Event checkEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
    }

    private LocalDateTime setDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(LocalDateTime.now().format(formatter), formatter);
    }
}
