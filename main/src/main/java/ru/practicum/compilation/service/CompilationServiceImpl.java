package ru.practicum.compilation.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private static final String TAG = "COMPILATION SERVICE";
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;

    //Admin API для работы с подборками событий
    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        log.info("{} - Обработка запроса на добавление подборки", TAG);

        List<Event> events = findEvents(newCompilationDto.getEvents());
        List<EventShortDto> eventShortDtos = eventMapper.toEventShortDtos(events);

        Compilation compilation = new Compilation();
        compilation.setTitle(newCompilationDto.getTitle());
        compilation.setPinned(newCompilationDto.isPinned());
        compilation.setEvents(events);

        Compilation response = compilationRepository.save(compilation);
        return compilationMapper.toCompilationDto(response, eventShortDtos);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        log.info("{} - Обработка запроса на обновление подборки", TAG);
        Compilation compilation = findCompilationById(compId);
        compilation.setTitle(updateCompilationRequest.getTitle());
        compilation.setPinned(updateCompilationRequest.isPinned());

        List<Event> events = findEvents(updateCompilationRequest.getEvents());
        List<EventShortDto> eventShortDtos = eventMapper.toEventShortDtos(events);

        compilation.setEvents(events);
        Compilation response = compilationRepository.save(compilation);
        return compilationMapper.toCompilationDto(response, eventShortDtos);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        log.info("{} - Обработка запроса на удаление подборки", TAG);
        findCompilationById(compId);
        compilationRepository.deleteById(compId);
    }

    //Public API для работы с подборками событий
    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, int from, int size) {
        log.info("{} - Обработка запроса на получение всех подборок", TAG);
        Pageable pageable = PageRequest.of(from / size, size);
        List<Compilation> compilations = new ArrayList<>();
        if (pinned != null) {
            compilations = compilationRepository.findAllByPinned(pinned, pageable);
        } else {
            compilations = compilationRepository.findAll(pageable).toList();
        }

        List<CompilationDto> response = new ArrayList<>();
        for (Compilation compilation : compilations) {
            response.add(compilationMapper.toCompilationDto(compilation, eventMapper.toEventShortDtos(compilation.getEvents())));
        }

        return response;
    }

    @Override
    public CompilationDto getCompilation(Long compId) {
        log.info("{} - Обработка запроса на получение подборки", TAG);
        Compilation compilation = findCompilationById(compId);
        List<Event> events = compilation.getEvents();
        List<EventShortDto> eventShortDtos = eventMapper.toEventShortDtos(events);

        return compilationMapper.toCompilationDto(compilation, eventShortDtos);
    }

    private Compilation findCompilationById(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка не найдена"));
    }

    private List<Event> findEvents(List<Long> eventIds) {
        List<Event> events = new ArrayList<>();
        if (eventIds != null && !eventIds.isEmpty()) {
            events = eventRepository.findAllByIdIn(eventIds);
        }
        return events;
    }
}
