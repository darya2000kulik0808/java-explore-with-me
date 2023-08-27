package ru.practicum.controllers.comment;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.services.comment.CommentService;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@Validated
@RequestMapping(path = "/users/{userId}/events/{eventId}/comments")
public class CommentPrivateController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentFullDto postComment(@PathVariable Long userId,
                                      @PathVariable Long eventId,
                                      @Valid @RequestBody NewCommentDto newCommentDto) {
        return commentService.postComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{commId}")
    public CommentFullDto editComment(@PathVariable Long userId,
                                      @PathVariable Long eventId,
                                      @PathVariable Long commId,
                                      @Valid @RequestBody NewCommentDto newCommentDto) {
        return commentService.editComment(userId, eventId, commId, newCommentDto);
    }

    @PatchMapping("/{commId}/likes")
    public CommentDto editComment(@PathVariable Long userId,
                                  @PathVariable Long eventId,
                                  @PathVariable Long commId) {
        return commentService.increaseLikesForComment(userId, eventId, commId);
    }
}