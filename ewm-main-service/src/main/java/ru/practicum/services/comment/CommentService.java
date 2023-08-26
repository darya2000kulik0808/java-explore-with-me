package ru.practicum.services.comment;

import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.dto.comment.NewCommentDto;

import java.util.List;

public interface CommentService {
    List<CommentFullDto> getAllForUser(Long userId);

    List<CommentDto> getAllForEvent(Long eventId);

    CommentFullDto postComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto increaseLikesForComment(Long userId, Long eventId, Long commId);

    List<CommentFullDto> getCommentsForAdmin(Long eventId);

    CommentFullDto editCommentByAdmin(Long eventId, Long commId, String actionAdmin);

    void deleteComment(Long eventId, Long commId);

    CommentFullDto editComment(Long userId, Long eventId, Long commId, NewCommentDto newCommentDto);
}
