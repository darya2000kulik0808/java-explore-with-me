package ru.practicum.dto.requests;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ParticipationRequestDto {
    Long id; //Идентификатор заявки
    LocalDateTime created; //Дата и время создания заявки
    Long event;// Идентификатор события
    Long requester;	// Идентификатор пользователя, отправившего заявку
    String status; // Статус заявки
}
