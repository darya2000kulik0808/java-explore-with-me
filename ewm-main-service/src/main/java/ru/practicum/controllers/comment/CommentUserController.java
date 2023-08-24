package ru.practicum.controllers.comment;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.services.comment.CommentService;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/users/{userId}/comments")
public class CommentUserController {

    private final CommentService commentService;

    @GetMapping
    public List<CommentFullDto> getAllForUser(@PathVariable Long userId) {
        return commentService.getAllForUser(userId);
    }
}