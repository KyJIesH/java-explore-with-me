package ru.practicum.category.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

@Controller
@Validated
@Slf4j
@AllArgsConstructor
@RequestMapping("/categories")
public class CategoryPublicController {
    private static final String TAG = "CategoryPublic CONTROLLER";
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> findCategory(@PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                          @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("{} - Пришел запрос на получение категории (GET /categories)", TAG);
        List<CategoryDto> response = categoryService.findCategories(from, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{catId}")
    public ResponseEntity<CategoryDto> findCategoryById(@PathVariable Long catId) {
        log.info("{} - Пришел запрос на получение категории по id {} (GET /categories)", TAG, catId);
        CategoryDto response = categoryService.findCategoryById(catId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
