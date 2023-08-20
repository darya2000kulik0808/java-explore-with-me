package ru.practicum.dto.compilation;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
public class NewCompilationDto {
    List<Long> events; //id событий в подборке
    Boolean pinned;
    @NotBlank(message = "Название подборки не может быть пустым.")
    @Size(max = 50, min = 1, message = "Длина строки не может превышать 50 символов и не может быть меньше 1 символа!")
    String title;
}
