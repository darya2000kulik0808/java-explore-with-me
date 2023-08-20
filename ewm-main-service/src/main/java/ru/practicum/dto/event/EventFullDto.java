package ru.practicum.dto.event;

import lombok.Builder;
import lombok.Data;
import ru.practicum.dto.Location;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.enums.EventStateEnum;

import java.time.LocalDateTime;

@Data
@Builder
public class EventFullDto {
    Long id;
    String annotation;
    CategoryDto category;
    Integer confirmedRequests; //Количество одобренных заявок на участие в данном событии
    LocalDateTime createdOn; //Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss")
    String description;
    LocalDateTime eventDate; //Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")
    UserShortDto initiator;
    Location location; //широта и долгота проведения события
    Boolean paid; // Нужно ли оплачивать участие
    Integer participantLimit; //default: 0 Ограничение на количество участников. Значение 0 - означает отсутствие ограничения
    LocalDateTime publishedOn; //Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss")
    Boolean requestModeration; //Нужна ли пре-модерация заявок на участие
    EventStateEnum state;
    String title;
    Integer views;
}
