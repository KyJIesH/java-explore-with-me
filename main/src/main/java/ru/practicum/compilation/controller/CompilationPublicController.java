package ru.practicum.compilation.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.CompilationService;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

@Controller
@Validated
@Slf4j
@AllArgsConstructor
@RequestMapping("/compilations")
public class CompilationPublicController {
    private static final String TAG = "CompilationPublic CONTROLLER";
    private final CompilationService compilationService;

    @GetMapping
    public ResponseEntity<List<CompilationDto>> getAllCompilations(@RequestParam(required = false) Boolean pinned,
                                                                   @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                                   @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("{} - Пришел запрос на получение всех подборок (GET /compilations)", TAG);
        List<CompilationDto> response = compilationService.getAllCompilations(pinned, from, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationDto> getCompilation(@PathVariable Long compId) {
        log.info("{} - Пришел запрос на получение подбороки по id {} (GET /compilations/{compId})", TAG, compId);
        CompilationDto response = compilationService.getCompilation(compId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
