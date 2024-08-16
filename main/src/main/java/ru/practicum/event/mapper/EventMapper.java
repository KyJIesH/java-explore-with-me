package ru.practicum.event.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class EventMapper {

    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;

    private static final String format = "yyyy-MM-dd HH:mm:ss";

    public Event toEvent(NewEventDto newEventDto, User user, Category category) {
        Event event = new Event();
        event.setId(null);
        event.setInitiator(user);
        event.setCreatedOn(getLocalDate());
        event.setPublishedOn(null);
        event.setTitle(newEventDto.getTitle());
        event.setAnnotation(newEventDto.getAnnotation());
        event.setDescription(newEventDto.getDescription());
        event.setCategory(category);
        event.setEventDate(newEventDto.getEventDate());
        event.setLocation(newEventDto.getLocation());
        event.setConfirmedRequests(0L);
        event.setPaid(newEventDto.isPaid());
        if (newEventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(newEventDto.getParticipantLimit());
        } else {
            event.setParticipantLimit(0L);
        }
        event.setRequestModeration(newEventDto.isRequestModeration());
        event.setState(State.PENDING);
        event.setViews(0L);

        return event;
    }

    public EventFullDto toEventFullDto(Event event) {
        CategoryDto categoryDto = categoryMapper.toCategoryDto(event.getCategory());
        UserShortDto userShortDto = userMapper.toUserShortDto(event.getInitiator());

        EventFullDto eventFullDto = new EventFullDto();
        eventFullDto.setId(event.getId());
        eventFullDto.setInitiator(userShortDto);
        eventFullDto.setCreatedOn(event.getCreatedOn());
        eventFullDto.setPublishedOn(event.getPublishedOn());
        eventFullDto.setTitle(event.getTitle());
        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setCategory(categoryDto);
        eventFullDto.setEventDate(event.getEventDate());
        eventFullDto.setLocation(event.getLocation());
        eventFullDto.setConfirmedRequests(event.getConfirmedRequests());
        eventFullDto.setPaid(event.isPaid());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setState(event.getState());
        eventFullDto.setViews(event.getViews());

        return eventFullDto;
    }

    public EventShortDto toEventShortDto(Event event) {
        EventShortDto eventShortDto = new EventShortDto();
        eventShortDto.setId(event.getId());
        eventShortDto.setInitiator(userMapper.toUserShortDto(event.getInitiator()));
        eventShortDto.setTitle(event.getTitle());
        eventShortDto.setAnnotation(event.getAnnotation());
        eventShortDto.setCategory(categoryMapper.toCategoryDto(event.getCategory()));
        eventShortDto.setEventDate(event.getEventDate());
        eventShortDto.setPaid(event.isPaid());
        eventShortDto.setConfirmedRequests(event.getConfirmedRequests());
        eventShortDto.setViews(event.getViews());

        return eventShortDto;
    }

    public UpdateEventUserRequest toUpdateEventUserRequest(UpdateEventAdminRequest updateEventAdminRequest) {
        UpdateEventUserRequest updateEventUserRequest = new UpdateEventUserRequest();
        if (updateEventAdminRequest.getTitle() != null) {
            updateEventUserRequest.setTitle(updateEventAdminRequest.getTitle());
        }
        if (updateEventAdminRequest.getAnnotation() != null) {
            updateEventUserRequest.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getDescription() != null) {
            updateEventUserRequest.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getCategory() != null) {
            updateEventUserRequest.setCategory(updateEventAdminRequest.getCategory());
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            updateEventUserRequest.setEventDate(updateEventAdminRequest.getEventDate());
        }
        if (updateEventAdminRequest.getLocation() != null) {
            updateEventUserRequest.setLocation(updateEventAdminRequest.getLocation());
        }
        if (updateEventAdminRequest.getPaid() != null) {
            updateEventUserRequest.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            updateEventUserRequest.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }

        if (updateEventAdminRequest.getStateAction() != null) {
            updateEventUserRequest.setStateAction(updateEventAdminRequest.getStateAction());
        }

        return updateEventUserRequest;
    }

    public List<EventShortDto> toEventShortDtos(List<Event> events) {
        List<EventShortDto> eventShortDtos = new ArrayList<>();
        for (Event event : events) {
            eventShortDtos.add(toEventShortDto(event));
        }
        return eventShortDtos;
    }

    public List<EventFullDto> toEventFullDtos(List<Event> events) {
        List<EventFullDto> eventFullDtos = new ArrayList<>();
        for (Event event : events) {
            eventFullDtos.add(toEventFullDto(event));
        }
        return eventFullDtos;
    }

    private static LocalDateTime getLocalDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(LocalDateTime.now().format(formatter), formatter);
    }
}
