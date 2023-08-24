package ru.practicum.services.category;

import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAllCategories(Integer from, Integer size);

    CategoryDto getOneCategory(Long catId);

    CategoryDto createNewCategory(NewCategoryDto category);

    CategoryDto patchCategory(Long catId, NewCategoryDto category);

    void deleteCategory(Long catId);
}
