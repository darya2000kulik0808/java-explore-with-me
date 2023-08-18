package ru.practicum;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EndpointHitDto {
    @NotBlank(message = "Название приложения не может быть пустым.")
    String app;
    @NotBlank(message = "Путь к эндпоинту приложения не может быть пустым.")
    String uri;
    @NotBlank(message = "IP-адрес, с которого был сделан запрос к эндпоинту приложения не может быть пустым.")
    String ip;
    @NotNull(message = "Время обращения не может быть пустым.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime timestamp; //формат: yyyy-MM-dd HH:mm:ss
}
