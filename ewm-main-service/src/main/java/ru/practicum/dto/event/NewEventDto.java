package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.dto.LocationDto;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    @NotBlank(message = "Краткое описание события не может быть пустым!")
    @Size(max = 2000, min = 20, message = "Длина строки не может превышать 2000 символов и не может быть меньше 20 символов!")
    private String annotation;
    @NotNull(message = "Категория не может быть пустой!")
    @Positive(message = "Идентификатор категории не может равняться 0!")
    private Long category; // id категории
    @NotBlank(message = "Полное описание события не может быть пустым!")
    @Size(max = 7000, min = 20, message = "Длина строки не может превышать 7000 символов и не может быть меньше 20 символов!")
    private String description;
    @NotNull(message = "Время события не может быть пустым!")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @FutureOrPresent(message = "Поле должно содержать дату, которая еще не наступила")
    private LocalDateTime eventDate; // Дата и время указываются в формате "yyyy-MM-dd HH:mm:ss"
    @NotNull(message = "Место проведения события не может быть пустым!")
    private LocationDto location;
    private Boolean paid; //default: false
    private Integer participantLimit; //default: 0
    private Boolean requestModeration; //Нужна ли пре-модерация заявок на участие.
    // Если true, то все заявки будут ожидать подтверждения инициатором события.
    // Если false - то будут подтверждаться автоматически.
    //default: true
    @NotBlank(message = "Название события не может быть пустым!")
    @Size(max = 120, min = 3, message = "Длина строки не может превышать 120 символов и не может быть меньше 3 символов!")
    private String title;
}
