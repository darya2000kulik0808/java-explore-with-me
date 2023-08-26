package ru.practicum.controllers.comment;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.services.comment.CommentService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/admin/events/{eventId}/comments")
public class CommentAdminController {

    private final CommentService commentService;

    @GetMapping
    public List<CommentFullDto> getCommentsForAdmin(@PathVariable Long eventId) {
        return commentService.getCommentsForAdmin(eventId);
    }

    @PatchMapping("/{commId}")
    public CommentFullDto editCommentByAdmin(@PathVariable Long eventId,
                                             @PathVariable Long commId,
                                             @RequestParam String actionAdmin) {
        return commentService.editCommentByAdmin(eventId, commId, actionAdmin);
    }

    @DeleteMapping("/{commId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long eventId,
                              @PathVariable Long commId) {
        commentService.deleteComment(eventId, commId);
    }
}