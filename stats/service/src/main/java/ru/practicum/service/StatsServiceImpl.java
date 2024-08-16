package ru.practicum.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.RequestStatsDto;
import ru.practicum.ResponseStatsDto;
import ru.practicum.StatsDto;
import ru.practicum.model.mappers.StatsMapper;
import ru.practicum.repository.StatsRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class StatsServiceImpl implements StatsService {

    private static final String TAG = "STATS SERVICE";
    private final StatsRepository statsRepository;
    private final StatsMapper statsMapper;

    @Override
    @Transactional
    public void createStats(StatsDto statsDto) {
        log.info("{} - Обработка запроса на добавление статистики {}", TAG, statsDto);
        statsRepository.save(statsMapper.toStat(statsDto));
    }

    @Override
    public List<ResponseStatsDto> getStats(RequestStatsDto requestStatsDto) {
        log.info("{} - Обработка запроса на получение статистики по посещениям {}", TAG, requestStatsDto);
        List<ResponseStatsDto> responseStatsDtos = new ArrayList<>();
        if (requestStatsDto.getUris() == null) {
            if (!requestStatsDto.isUnique()) {
                responseStatsDtos = statsRepository.findAllByTime(requestStatsDto.getStart(), requestStatsDto.getEnd());
            } else {
                responseStatsDtos = statsRepository.findAllByTimeUnique(requestStatsDto.getStart(), requestStatsDto.getEnd());
            }
        } else {
            if (!requestStatsDto.isUnique()) {
                responseStatsDtos = statsRepository.findAllByTimeAndUris(requestStatsDto.getStart(), requestStatsDto.getEnd(),
                        requestStatsDto.getUris());
            } else {
                responseStatsDtos = statsRepository.findAllByTimeAndUrisUnique(requestStatsDto.getStart(), requestStatsDto.getEnd(),
                        requestStatsDto.getUris());
            }
        }
        return responseStatsDtos;
    }
}
