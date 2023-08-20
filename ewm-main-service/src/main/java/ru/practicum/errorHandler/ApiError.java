package ru.practicum.errorHandler;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ApiError {

    List<String> errors; //Список стектрейсов или описания ошибок

    String message; // Сообщение об ошибке

    String reason; // Общее описание причины ошибки

    HttpStatus status; // Код статуса HTTP-ответа

    LocalDateTime timestamp; //Дата и время когда произошла ошибка (в формате "yyyy-MM-dd HH:mm:ss")
}
