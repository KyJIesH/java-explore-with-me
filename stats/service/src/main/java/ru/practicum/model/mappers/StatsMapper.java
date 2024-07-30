package ru.practicum.model.mappers;

import org.mapstruct.Mapper;
import ru.practicum.StatsDto;
import ru.practicum.model.Stats;

@Mapper(componentModel = "spring", uses = StatsDto.class)
public interface StatsMapper {

    Stats toStat(StatsDto statsDto);
}
