package ru.practicum.controllers.category;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.services.category.CategoryService;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/admin/categories")
public class CategoriesAdminController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createNewCompilation(@Valid @RequestBody NewCategoryDto category){
        log.info("Получили запрос на создание категории.");
        return categoryService.createNewCategory(category);
    }

    @PatchMapping("/{catId}")
    public CategoryDto patchCategory(@PathVariable Long catId,
                                     @Valid @RequestBody NewCategoryDto category) {
        log.info("Получили запрос на изменение категории.");
        return categoryService.patchCategory(catId, category);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        log.info("Получили запрос на удаление категории.");
        categoryService.deleteCategory(catId);
    }
}
