package ru.practicum.dto.event;

import lombok.Builder;
import lombok.Data;
import ru.practicum.dto.Location;
import ru.practicum.enums.StateActionAdminEnum;
import ru.practicum.enums.StateActionUserEnum;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
public class UpdateEventUserRequest {
    //Данные для изменения информации о событии.
    // Если поле в запросе не указано (равно null) - значит изменение этих данных не треубется.

    @Size(max = 2000, min = 20, message = "Длина строки не может превышать 2000 символов и не может быть меньше 20 символов!")
    String annotation;
    Long category;
    @Size(max = 7000, min = 20, message = "Длина строки не может превышать 7000 символов и не может быть меньше 20 символов!")
    String description;
    LocalDateTime eventDate; //Новые дата и время на которые намечено событие.
    // Дата и время указываются в формате "yyyy-MM-dd HH:mm:ss"
    Location location;
    Boolean paid;
    Integer participantLimit;
    Boolean requestModeration;
    StateActionUserEnum stateAction;
    @Size(max = 120, min = 3, message = "Длина строки не может превышать 120 символов и не может быть меньше 3 символов!")
    String title;
}
