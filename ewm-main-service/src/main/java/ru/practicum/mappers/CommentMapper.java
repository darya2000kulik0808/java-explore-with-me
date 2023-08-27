package ru.practicum.mappers;

import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.enums.CommentStatusEnum;
import ru.practicum.models.Comment;
import ru.practicum.models.Event;
import ru.practicum.models.User;

import java.time.LocalDateTime;

public class CommentMapper {

    public static CommentFullDto toCommentFullDto(Comment comment) {
        return CommentFullDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .author(UserMapper.toUserShortDto(comment.getAuthor()))
                .event(EventMapper.toEventForComment(comment.getEvent()))
                .created(comment.getCreated())
                .likes(comment.getLikes())
                .status(comment.getStatus())
                .build();
    }

    public static Comment toComment(NewCommentDto newCommentDto, User user, Event event) {
        return Comment.builder()
                .text(newCommentDto.getText())
                .author(user)
                .event(event)
                .likes(0)
                .status(CommentStatusEnum.PENDING)
                .created(LocalDateTime.now())
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .author(UserMapper.toUserShortDto(comment.getAuthor()))
                .created(comment.getCreated())
                .likes(comment.getLikes())
                .text(comment.getText())
                .build();
    }
}
