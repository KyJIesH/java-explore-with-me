package ru.practicum.model.mappers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.StatsDto;
import ru.practicum.model.Stats;

import java.time.LocalDateTime;

@SpringBootTest
class StatsMapperTest {

    @Autowired
    private StatsMapper statsMapper;

    @Test
    void toStatTest() {
        StatsDto statsDto = new StatsDto(1L, "ewm-main-service", "/events/1", "198.168.0.0",
                LocalDateTime.of(2024, 7, 29, 22, 30, 0));

        Stats stats = statsMapper.toStat(statsDto);

        Assertions.assertNotNull(stats);
        Assertions.assertEquals(stats.getId(), statsDto.getId());
        Assertions.assertEquals(stats.getApp(), statsDto.getApp());
        Assertions.assertEquals(stats.getUri(), statsDto.getUri());
        Assertions.assertEquals(stats.getIp(), statsDto.getIp());
        Assertions.assertEquals(stats.getTimestamp(), statsDto.getTimestamp());
    }
}