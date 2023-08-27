package ru.practicum.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.dto.event.EventForComment;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.enums.CommentStatusEnum;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentFullDto {
    private Long id;
    private String text;
    private UserShortDto author;
    private EventForComment event;
    private CommentStatusEnum status;
    private Integer likes;
    private LocalDateTime created;
}