package ru.practicum.request.service;

import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    ParticipationRequestDto createRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> findRequest(Long userId);

    ParticipationRequestDto updateRequest(Long userId, Long requestId);
}
