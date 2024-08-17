package ru.practicum.category.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private static final String TAG = "CATEGORY SERVICE";
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        log.info("{} - Обработка запроса на добавление категории {}", TAG, newCategoryDto);
        Category response = categoryMapper.toCategory(newCategoryDto);

        try {
            response = categoryRepository.save(response);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage(), e);
        }

        return categoryMapper.toCategoryDto(response);
    }

    @Override
    public List<CategoryDto> findCategories(int from, int size) {
        log.info("{} - Обработка запроса на получение категории", TAG);
        List<Category> response = new ArrayList<>();
        Pageable pageable = PageRequest.of(from / size, size);
        response = categoryRepository.findAll(pageable).getContent();
        return categoryMapper.toCategoryDtoList(response);
    }

    @Override
    public CategoryDto findCategoryById(Long catId) {
        log.info("{} - Обработка запроса на получение категории по id {}", TAG, catId);
        Category response = checkCategoryByCategoryId(catId);
        return categoryMapper.toCategoryDto(response);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        log.info("{} - Обработка запроса на обновление категории", TAG);
        Category update = categoryMapper.toCategory(categoryDto);
        Category category = checkCategoryByCategoryId(catId);

        if (category.getName().equals(update.getName())) {
            return categoryMapper.toCategoryDto(categoryRepository.save(category));
        } else {
            try {
                categoryRepository.save(update);
            } catch (DataIntegrityViolationException e) {
                throw new ConflictException(e.getMessage(), e);
            }
            return categoryMapper.toCategoryDto(update);
        }
    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        log.info("{} - Обработка запроса на удаление категории", TAG);
        checkCategoryByCategoryId(catId);
        List<Event> events = eventRepository.findAllByCategoryId(catId);
        if (!events.isEmpty()) {
            throw new ConflictException("Существуют события, связанные с категорией");
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    public Category checkCategoryByCategoryId(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория не найдена"));
    }
}
