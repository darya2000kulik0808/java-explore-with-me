package ru.practicum.controllers.category;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.services.category.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.errorHandler.ErrorMessages.FROM_ERROR_MESSAGE;
import static ru.practicum.errorHandler.ErrorMessages.SIZE_ERROR_MESSAGE;

@RestController
@AllArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/categories")
public class CategoriesPublicController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getAllCategories(@PositiveOrZero(message = FROM_ERROR_MESSAGE)
                                              @RequestParam(defaultValue = "0") Integer from,
                                              @Positive(message = SIZE_ERROR_MESSAGE)
                                              @RequestParam(defaultValue = "10") Integer size){
        log.info("Получили запрос на получение всех категорий.");
        return categoryService.getAllCategories(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto getOneCategory(@PathVariable Long catId){
        log.info("Получили запрос на получение одной категории с id: {}.", catId);
        return categoryService.getOneCategory(catId);
    }
}
