package ru.practicum.dto.compilation;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
public class UpdateCompilationRequest {
    //Изменение информации о подборке событий.
    // Если поле в запросе не указано (равно null) - значит изменение этих данных не треубется.

    List<Long> events;	//Список id событий подборки для полной замены текущего списка
    Boolean pinned;//Закреплена ли подборка на главной странице сайта
    @Size(max = 50, min = 1, message = "Длина строки не может превышать 50 символов и не может быть меньше 1 символа!")
    String title;
}
