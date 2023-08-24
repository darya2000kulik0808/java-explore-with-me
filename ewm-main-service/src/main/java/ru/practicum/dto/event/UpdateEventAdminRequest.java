package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.dto.LocationDto;
import ru.practicum.enums.StateActionAdminEnum;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventAdminRequest {
    //Данные для изменения информации о событии.
    // Если поле в запросе не указано (равно null) - значит изменение этих данных не треубется.

    @Size(max = 2000, min = 20, message = "Длина строки не может превышать 2000 символов и не может быть меньше 20 символов!")
    private String annotation;
    private Long category;
    @Size(max = 7000, min = 20, message = "Длина строки не может превышать 7000 символов и не может быть меньше 20 символов!")
    private String description;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @FutureOrPresent(message = "Поле должно содержать дату, которая еще не наступила")
    private LocalDateTime eventDate; //Новые дата и время на которые намечено событие.
    // Дата и время указываются в формате "yyyy-MM-dd HH:mm:ss"
    private LocationDto locationDto;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private StateActionAdminEnum stateAction;
    @Size(max = 120, min = 3, message = "Длина строки не может превышать 120 символов и не может быть меньше 3 символов!")
    private String title;
}
