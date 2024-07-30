package ru.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.RequestStatsDto;
import ru.practicum.ResponseStatsDto;
import ru.practicum.StatsDto;
import ru.practicum.model.mappers.StatsMapper;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsServiceImplTest {

    @Mock
    private StatsRepository statsRepository;

    @Mock
    private StatsMapper statsMapper;

    private StatsServiceImpl statsService;

    private StatsDto statsDto;
    private List<ResponseStatsDto> responseStatsDtos;

    @BeforeEach
    void setUp() {
        statsService = new StatsServiceImpl(statsRepository, statsMapper);

        statsDto = new StatsDto(1L, "ewm-main-service", "/events/1", "198.168.0.0",
                LocalDateTime.of(2024, 7, 29, 22, 30, 0));

        ResponseStatsDto responseStatsDto = new ResponseStatsDto("app-main-service", "/events/1", 1L);

        responseStatsDtos = Collections.singletonList(responseStatsDto);
    }

    @Test
    void createStatsTest() {
        statsService.createStats(statsDto);
        verify(statsRepository).save(any());
    }

    @Test
    void getStatsByTimeTest() {
        RequestStatsDto request = new RequestStatsDto(
                LocalDateTime.of(2020, 1, 1, 1, 30, 0),
                LocalDateTime.of(2024, 7, 29, 22, 30, 0),
                null, false);

        when(statsRepository.findAllByTime(any(), any())).thenReturn(responseStatsDtos);

        List<ResponseStatsDto> response = statsService.getStats(request);
        assertEquals(1, response.size());

        verify(statsRepository).findAllByTime(any(), any());
        verify(statsRepository, never()).findAllByTimeUnique(any(), any());
        verify(statsRepository, never()).findAllByTimeUnique(any(), any());
        verify(statsRepository, never()).findAllByTimeAndUrisUnique(any(), any(), any());
    }

    @Test
    void getStatsByTimeUniqueTest() {
        RequestStatsDto request = new RequestStatsDto(
                LocalDateTime.of(2020, 1, 1, 1, 30, 0),
                LocalDateTime.of(2024, 7, 29, 22, 30, 0),
                null, true);

        when(statsRepository.findAllByTimeUnique(any(), any())).thenReturn(responseStatsDtos);

        List<ResponseStatsDto> response = statsService.getStats(request);
        assertEquals(1, response.size());

        verify(statsRepository).findAllByTimeUnique(any(), any());
        verify(statsRepository, never()).findAllByTime(any(), any());
        verify(statsRepository, never()).findAllByTimeAndUris(any(), any(), any());
        verify(statsRepository, never()).findAllByTimeAndUrisUnique(any(), any(), any());
    }

    @Test
    void getStatsByTimeAndUrisTest() {
        RequestStatsDto request = new RequestStatsDto(
                LocalDateTime.of(2020, 1, 1, 1, 30, 0),
                LocalDateTime.of(2024, 7, 29, 22, 30, 0),
                Collections.singletonList("/events/1"), false);

        when(statsRepository.findAllByTimeAndUris(any(), any(), any())).thenReturn(responseStatsDtos);

        List<ResponseStatsDto> response = statsService.getStats(request);
        assertEquals(1, response.size());

        verify(statsRepository).findAllByTimeAndUris(any(), any(), any());
        verify(statsRepository, never()).findAllByTime(any(), any());
        verify(statsRepository, never()).findAllByTimeUnique(any(), any());
        verify(statsRepository, never()).findAllByTimeAndUrisUnique(any(), any(), any());
    }

    @Test
    void getStatsByTimeAndUrisUniqueTest() {
        RequestStatsDto request = new RequestStatsDto(
                LocalDateTime.of(2020, 1, 1, 1, 30, 0),
                LocalDateTime.of(2024, 7, 29, 22, 30, 0),
                Collections.singletonList("/events/1"), true);

        when(statsRepository.findAllByTimeAndUrisUnique(any(), any(), any())).thenReturn(responseStatsDtos);

        List<ResponseStatsDto> response = statsService.getStats(request);
        assertEquals(1, response.size());

        verify(statsRepository).findAllByTimeAndUrisUnique(any(), any(), any());
        verify(statsRepository, never()).findAllByTime(any(), any());
        verify(statsRepository, never()).findAllByTimeUnique(any(), any());
        verify(statsRepository, never()).findAllByTimeAndUris(any(), any(), any());
    }
}