package ru.practicum.compilation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dto.EventShortDto;

import java.util.List;

@Mapper(componentModel = "spring", uses = CompilationDto.class)
public interface CompilationMapper {

    @Mapping(target = "title", expression = "java(compilation.getTitle())")
    @Mapping(target = "pinned", expression = "java(compilation.isPinned())")
    @Mapping(target = "events", expression = "java(events)")
    CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> events);

    CompilationDto toCompilationDto(Compilation compilation);

    List<CompilationDto> toCompilationDtos(List<Compilation> compilations);
}
