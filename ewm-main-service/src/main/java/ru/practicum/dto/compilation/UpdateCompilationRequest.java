package ru.practicum.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCompilationRequest {
    //Изменение информации о подборке событий.
    // Если поле в запросе не указано (равно null) - значит изменение этих данных не треубется.

    private List<Long> events;    //Список id событий подборки для полной замены текущего списка
    private Boolean pinned;//Закреплена ли подборка на главной странице сайта
    @Size(max = 50, min = 1, message = "Длина строки не может превышать 50 символов и не может быть меньше 1 символа!")
    private String title;
}
