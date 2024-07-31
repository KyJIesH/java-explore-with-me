package ru.practicum.service;

import ru.practicum.RequestStatsDto;
import ru.practicum.ResponseStatsDto;
import ru.practicum.StatsDto;

import java.util.List;

public interface StatsService {

    void createStats(StatsDto statsDto);

    List<ResponseStatsDto> getStats(RequestStatsDto requestStatsDto);
}
