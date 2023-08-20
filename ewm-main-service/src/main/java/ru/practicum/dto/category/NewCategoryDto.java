package ru.practicum.dto.category;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
public class NewCategoryDto {
    @NotBlank(message = "Название категории не может быть пустым.")
    @Size(max = 50, min = 1, message = "Длина строки не может превышать 50 символов и не может быть меньше 1 символа!")
    String name;
}
