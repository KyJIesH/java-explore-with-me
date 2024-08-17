package ru.practicum.event.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.StatsDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.*;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventSearch;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.location.model.Location;
import ru.practicum.location.repository.LocationRepository;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.Status;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ru.practicum.event.model.State.*;

@Service
@Slf4j
@AllArgsConstructor
@ComponentScan(basePackages = {"ru.practicum.client"})
public class EventServiceImpl implements EventService {

    private static final String TAG = "EVENT SERVICE";
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;
    private final StatsClient statsClient;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    //Закрытый API для работы с событиями
    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        log.info("{} - Обработка запроса на добавление события {}", TAG, newEventDto);
        User user = userService.findUserById(userId);
        validateCreateEvent(newEventDto.getEventDate());
        Category category = checkCategoryByCategoryId(newEventDto.getCategory());
        locationRepository.save(newEventDto.getLocation());
        Event response = eventRepository.save(eventMapper.toEvent(newEventDto, user, category));
        if (!newEventDto.isPaid() && newEventDto.getParticipantLimit() == null && !newEventDto.isRequestModeration()) {
            response.setRequestModeration(true);
        }
        return eventMapper.toEventFullDto(response);
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        log.info("{} - Обработка запроса на обновление события с id {}", TAG, eventId);
        userService.findUserById(userId);
        Event event = checkEventByUserIdAndEventId(userId, eventId);
        if (event.getState().equals(PUBLISHED)) {
            throw new ConflictException("Изменить можно только отмененные события или события в состоянии ожидания модерации");
        }
        Event response = eventRepository.save(updateEvent(event, updateEventUserRequest));
        return eventMapper.toEventFullDto(response);
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateStatusEvent(Long userId, Long eventId,
                                                            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("{} - Обработка запроса на изменение статуса (подтверждена, отменена) заявок на участие в событии", TAG);
        userService.findUserById(userId);
        Event event = checkEventByUserIdAndEventId(userId, eventId);

        List<Request> confirmed = new ArrayList<>();
        List<Request> rejected = new ArrayList<>();
        int countRequests = eventRequestStatusUpdateRequest.getRequestIds().size();

        Status status = eventRequestStatusUpdateRequest.getStatus();
        List<Request> requests = requestRepository.findByIdIn(eventRequestStatusUpdateRequest.getRequestIds());

        for (Request request : requests) {
            if (!request.getStatus().equals(Status.PENDING)) {
                throw new ConflictException("Статус можно изменить только у заявок, находящихся в состоянии ожидания");
            }
        }

        switch (status) {
            case CONFIRMED:
                if (event.getParticipantLimit() > event.getConfirmedRequests() + countRequests ||
                        event.getParticipantLimit() == 0 ||
                        !event.getRequestModeration()) {
                    for (Request request : requests) {
                        request.setStatus(Status.CONFIRMED);
                    }
                    event.setConfirmedRequests(event.getConfirmedRequests() + countRequests);
                    confirmed.addAll(requests);
                } else if (event.getParticipantLimit() <= event.getConfirmedRequests()) {
                    throw new ConflictException("Уже достигнут лимит по заявкам на данное событие");
                } else {
                    for (Request request : requests) {
                        if (event.getParticipantLimit() > event.getConfirmedRequests()) {
                            request.setStatus(Status.CONFIRMED);
                            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                            confirmed.add(request);
                        } else {
                            request.setStatus(Status.REJECTED);
                            rejected.add(request);
                        }
                    }
                }
                break;
            case REJECTED:
                for (Request request : requests) {
                    request.setStatus(Status.REJECTED);
                }
                rejected.addAll(requests);
        }

        eventRepository.save(event);
        requestRepository.saveAll(requests);

        List<ParticipationRequestDto> confirmedRequests = requestMapper.toParticipationRequestDtos(confirmed);
        List<ParticipationRequestDto> rejectedRequests = requestMapper.toParticipationRequestDtos(rejected);

        EventRequestStatusUpdateResult response = new EventRequestStatusUpdateResult();
        response.setConfirmedRequests(confirmedRequests);
        response.setRejectedRequests(rejectedRequests);

        return response;
    }

    @Override
    public EventFullDto findEventByUserIdAndEventId(Long userId, Long eventId) {
        log.info("{} - Обработка запроса на получение события с id {}", TAG, eventId);
        userService.findUserById(userId);
        Event response = checkEventByUserIdAndEventId(userId, eventId);
        return eventMapper.toEventFullDto(response);
    }

    @Override
    public List<EventShortDto> findAllEventsByUserId(Long userId, int from, int size) {
        log.info("{} - Обработка запроса на получение всех событиий пользователя с id {}", TAG, userId);
        List<Event> response = new ArrayList<>();
        Pageable pageable = PageRequest.of(from / size, size);
        userService.findUserById(userId);
        response = eventRepository.findAllByInitiatorId(userId, pageable);
        return eventMapper.toEventShortDtos(response);
    }

    @Override
    public List<ParticipationRequestDto> findParticipationByUserIdAndEventId(Long userId, Long eventId) {
        log.info("{} - Обработка запроса на получение информации о запросах на участие", TAG);
        userService.findUserById(userId);
        checkEventByUserIdAndEventId(userId, eventId);
        List<Request> response = requestRepository.findAllByEventId(eventId);
        return requestMapper.toParticipationRequestDtos(response);
    }

    //Admin API для работы с событиями
    @Override
    public List<EventFullDto> findAdminAllEvents(EventAdminFindParam eventAdminFindParam) {
        log.info("{} - Обработка запроса на получение всех событий", TAG);

        Pageable pageable = PageRequest.of(eventAdminFindParam.getFrom() / eventAdminFindParam.getSize(),
                eventAdminFindParam.getSize());

        List<Event> events = new ArrayList<>();

        if (eventAdminFindParam.getRangeStart() == null && eventAdminFindParam.getRangeEnd() == null) {
            events = eventRepository.findAdminAllEventsWithoutRange(
                    eventAdminFindParam.getUsers(),
                    eventAdminFindParam.getStates(),
                    eventAdminFindParam.getCategories(),
                    pageable);
        } else {
            events = eventRepository.findAdminAllEvents(
                    eventAdminFindParam.getUsers(),
                    eventAdminFindParam.getStates(),
                    eventAdminFindParam.getCategories(),
                    eventAdminFindParam.getRangeStart(),
                    eventAdminFindParam.getRangeEnd(),
                    pageable);
        }

        return eventMapper.toEventFullDtos(events);
    }

    @Override
    @Transactional
    public EventFullDto updateStatusEventAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("{} - Обработка запроса на обновление данных события и его статуса (отклонение/публикация)", TAG);
        Event event = findEventById(eventId);

        if (updateEventAdminRequest.getEventDate() != null) {
            validateCreateEvent(updateEventAdminRequest.getEventDate());
            event.setEventDate(updateEventAdminRequest.getEventDate());
        } else {
            if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                throw new ValidationException("Дата и время на которые намечено событие не может быть раньше, " +
                        "чем через час от текущего момента");
            }
        }
        if (!event.getState().equals(State.PENDING)) {
            throw new ConflictException("Событие не находится в состоянии ожидания публикации");
        }

        UpdateEventUserRequest update = eventMapper.toUpdateEventUserRequest(updateEventAdminRequest);

        Event response = eventRepository.save(updateEvent(event, update));

        return eventMapper.toEventFullDto(response);
    }

    //Public API для работы с событиями
    @Override
    public List<EventShortDto> findEventsParam(EventUserFindParam eventUserFindParam, HttpServletRequest request) {
        log.info("{} - Обработка запроса на получение событий с возможностью фильтрации", TAG);
        Pageable pageable = sort(eventUserFindParam.getFrom(), eventUserFindParam.getSize(), eventUserFindParam.getSort());

        EventSearch searchParam = new EventSearch(
                eventUserFindParam.getText(),
                eventUserFindParam.getCategories(),
                eventUserFindParam.getPaid(),
                eventUserFindParam.getRangeStart(),
                eventUserFindParam.getRangeEnd(),
                eventUserFindParam.getOnlyAvailable()
        );

        validateRangeTime(searchParam.getRangeStart(), searchParam.getRangeEnd());

        List<Event> response = eventRepository.findAllByParam(searchParam, pageable).toList();
        saveStat(request);
        return eventMapper.toEventShortDtos(response);
    }

    @Override
    public EventFullDto findEventPublishedById(Long eventId, HttpServletRequest request) {
        log.info("{} - Обработка запроса на получение подробной информации об опубликованном событии по id {}", TAG, eventId);
        Event event = findEventById(eventId);
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException("Событие не опубликовано");
        }

        saveStat(request);

        List<String> uris = new ArrayList<>();
        uris.add(request.getRequestURI());

        ObjectMapper objectMapper = new ObjectMapper();

        ResponseEntity<Object> response =
                statsClient.getStats(LocalDateTime.now().minusDays(10).format(formatter),
                        LocalDateTime.now().format(formatter), uris, true);

        List<Map<String, String>> json =
                objectMapper.convertValue(response.getBody(), new TypeReference<List<Map<String, String>>>() {
                });
        if (!json.isEmpty()) {
            if (json.get(0).get("uri").equals(request.getRequestURI())) {
                event.setViews(Long.parseLong(json.get(0).get("hits")));
            }
        }
        eventRepository.save(event);
        return eventMapper.toEventFullDto(event);
    }

    private void saveStat(HttpServletRequest request) {
        StatsDto statsDto = new StatsDto();
        statsDto.setApp("AppMain");
        statsDto.setUri(request.getRequestURI());
        statsDto.setIp(request.getRemoteAddr());
        statsDto.setTimestamp(LocalDateTime.now());

        statsClient.createStats(statsDto);
    }

    @Override
    public Event findEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
    }

    private Pageable sort(int from, int size, String sort) {
        Pageable pageable = null;
        if (sort == null || sort.equalsIgnoreCase("EVENT_DATE")) {
            pageable = PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "event_date"));
        } else if (sort.equalsIgnoreCase("VIEWS")) {
            pageable = PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "views"));
        }
        return pageable;
    }

    private void validateCreateEvent(LocalDateTime timeEvent) {
        if (timeEvent.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Дата и время на которые намечено событие не может быть раньше, " +
                    "чем через два часа от текущего момента");
        }
    }

    private void validateRangeTime(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("Начало отрезка времени должно быть раньше его окончания");
        }
    }

    private Event checkEventByUserIdAndEventId(Long userId, Long eventId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
    }

    private Category checkCategoryByCategoryId(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Категория не найдена"));
    }

    private Location addLocation(Location location) {
        return locationRepository.save(location);
    }

    private LocalDateTime getLocalDateFormat() {
        return LocalDateTime.parse(LocalDateTime.now().format(formatter), formatter);
    }

    private Event updateEvent(Event event, UpdateEventUserRequest updateEventUserRequest) {
        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }
        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getCategory() != null) {
            event.setCategory(checkCategoryByCategoryId(updateEventUserRequest.getCategory()));
        }
        if (updateEventUserRequest.getEventDate() != null) {
            validateCreateEvent(updateEventUserRequest.getEventDate());
            event.setEventDate(updateEventUserRequest.getEventDate());
        }
        if (updateEventUserRequest.getLocation() != null) {
            event.setLocation(addLocation(updateEventUserRequest.getLocation()));
        }
        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }

        if (updateEventUserRequest.getStateAction() != null) {
            switch (updateEventUserRequest.getStateAction()) {
                case SEND_TO_REVIEW: {
                    event.setState(PENDING);
                    break;
                }
                case CANCEL_REVIEW: {
                    event.setState(CANCELED);
                    break;
                }
                case PUBLISH_EVENT: {
                    event.setState(PUBLISHED);
                    event.setPublishedOn(getLocalDateFormat());
                    break;
                }
                case REJECT_EVENT: {
                    event.setState(REJECTED);
                    break;
                }
            }
        }
        return event;
    }
}
