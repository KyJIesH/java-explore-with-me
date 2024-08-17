package ru.practicum.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.RequestStatsDto;
import ru.practicum.ResponseStatsDto;
import ru.practicum.StatsDto;
import ru.practicum.service.StatsService;
import ru.practicum.validation.ValidationDate;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
public class StatsController {

    private static final String TAG = "STATS CONTROLLER";
    private final StatsService statsService;

    @PostMapping("/hit")
    public ResponseEntity<String> createStat(@Valid @RequestBody StatsDto statsDto) {
        log.info("{} - Пришел запрос на сохранение информации о том, " +
                "что на uri конкретного сервиса был отправлен запрос пользователем (POST /hit)", TAG);
        statsService.createStats(statsDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ResponseStatsDto>> getStats(@Valid RequestStatsDto requestStatsDto) {
        log.info("{} - Пришел запрос на получение статистики по посещениям (GET /stats)", TAG);
        ValidationDate validator = new ValidationDate();
        validator.validationDate(requestStatsDto);
        List<ResponseStatsDto> responseStatsDtos = statsService.getStats(requestStatsDto);
        return new ResponseEntity<>(responseStatsDtos, HttpStatus.OK);
    }
}