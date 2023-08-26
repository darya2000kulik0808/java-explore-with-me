package ru.practicum.controllers.comment;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.services.comment.CommentService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/events/{eventId}/comments")
public class CommentPublicController {

    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getCommentsForEvent(@PathVariable Long eventId) {
        return commentService.getAllForEvent(eventId);
    }
}
