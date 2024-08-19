package ru.practicum.category.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.service.CategoryService;

import jakarta.validation.Valid;

@Controller
@Validated
@Slf4j
@AllArgsConstructor
@RequestMapping("/admin/categories")
public class CategoryAdminController {
    private static final String TAG = "CategoryAdmin CONTROLLER";
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("{} - Пришел запрос на добавление категории (POST /admin/categories)", TAG);
        CategoryDto response = categoryService.createCategory(newCategoryDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("{catId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long catId,
                                                      @Valid @RequestBody CategoryDto categoryDto) {
        log.info("{} - Пришел запрос на обновление категории (PATCH /admin/categories/{catId})", TAG);
        CategoryDto response = categoryService.updateCategory(catId, categoryDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("{catId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long catId) {
        log.info("{} - Пришел запрос на удаление категории (DELETE /admin/categories/{catId})", TAG);
        categoryService.deleteCategory(catId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
