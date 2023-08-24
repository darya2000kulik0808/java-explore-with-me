package ru.practicum.services.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.errorHandler.exceptions.ConflictException;
import ru.practicum.errorHandler.exceptions.ObjectNotFoundException;
import ru.practicum.mappers.CategoryMapper;
import ru.practicum.models.Category;
import ru.practicum.repositories.CategoryRepository;
import ru.practicum.repositories.EventRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        log.debug("Переходим к выборке катеорий из БД...");
        PageRequest page = PageRequest.of(from / size, size);
        List<Category> categories = categoryRepository.findAll(page).getContent();
        if (categories.isEmpty()) {
            return Collections.emptyList();
        }
        return categories.stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getOneCategory(Long catId) {
        log.debug("Переходим к выборке катеори с id={} из БД...", catId);
        return CategoryMapper.toCategoryDto(categoryRepository.findById(catId)
                .orElseThrow(() -> new ObjectNotFoundException(
                        String.format("Category with id=%d was not found", catId))));
    }

    @Override
    @Transactional
    public CategoryDto createNewCategory(NewCategoryDto category) {
        log.debug("Сохраняем катеорию в БД...");
        Category categoryToSave = CategoryMapper.toCategory(category);

        Category categoryByName = categoryRepository.findByName(category.getName());

        if (categoryByName != null) {
            throw new ConflictException(
                    String.format("Нарушение уникальности категории. Категория \"%s\" уже существует.",
                            category.getName()));
        }
        Category categorySaved = categoryRepository.save(categoryToSave);
        log.debug("Успешно сохранили категорию...");
        return CategoryMapper.toCategoryDto(categorySaved);
    }

    @Override
    @Transactional
    public CategoryDto patchCategory(Long catId, NewCategoryDto category) {
        log.debug("Обновляем катеорию в БД...");
        Category categoryToPatch = categoryRepository.findById(catId)
                .orElseThrow(() -> new ObjectNotFoundException(
                        String.format("Category with id=%d was not found", catId)));

        Category categoryByName = categoryRepository.findByName(category.getName());

        if (categoryByName != null) {
            if (categoryByName.equals(categoryToPatch)) {
                return CategoryMapper.toCategoryDto(categoryToPatch);
            }

            if (categoryByName.getName().equals(category.getName()) ||
                    categoryByName.getName().equals(categoryToPatch.getName())) {
                throw new ConflictException(
                        String.format("Нарушение уникальности категории. Категория \"%s\" уже существует.",
                                category.getName()));
            }
        }

        categoryToPatch.setName(category.getName());
        Category categoryPatched = categoryRepository.save(categoryToPatch);
        log.debug("Успешно обновили категорию...");
        return CategoryMapper.toCategoryDto(categoryPatched);
    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        categoryRepository.findById(catId)
                .orElseThrow(() -> new ObjectNotFoundException(
                        String.format("Category with id=%d was not found", catId)));
        Integer eventsInCategory = eventRepository.countAllByCategory_Id(catId);
        if (eventsInCategory == 0) {
            categoryRepository.deleteById(catId);
        } else {
            throw new ConflictException("The category is not empty");
        }
    }
}
