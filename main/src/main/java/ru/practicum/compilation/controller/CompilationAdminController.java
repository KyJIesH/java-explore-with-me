package ru.practicum.compilation.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.service.CompilationService;

import jakarta.validation.Valid;

@Controller
@Validated
@Slf4j
@AllArgsConstructor
@RequestMapping("/admin/compilations")
public class CompilationAdminController {
    private static final String TAG = "CompilationAdmin CONTROLLER";
    private final CompilationService compilationService;

    @PostMapping
    public ResponseEntity<CompilationDto> createCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("{} - Пришел запрос на создание подборки (POST /admin/compilations)", TAG);
        CompilationDto response = compilationService.createCompilation(newCompilationDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationDto> updateCompilation(@PathVariable Long compId,
                                                            @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        log.info("{} - Пришел запрос на обновление информации о подборке (PATCH /admin/compilations/{compId})", TAG);
        CompilationDto response = compilationService.updateCompilation(compId, updateCompilationRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<String> delete(@PathVariable Long compId) {
        log.info("{} - Пришел запрос на удаление подборки (DELETE /admin/compilations/{compId})", TAG);
        compilationService.deleteCompilation(compId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
