package ru.practicum.services.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.enums.CommentStatusEnum;
import ru.practicum.enums.RequestStatusEnum;
import ru.practicum.enums.StateActionAdminCommentEnum;
import ru.practicum.errorHandler.exceptions.CommentPostDenyException;
import ru.practicum.errorHandler.exceptions.ConflictException;
import ru.practicum.errorHandler.exceptions.ObjectNotFoundException;
import ru.practicum.mappers.CommentMapper;
import ru.practicum.models.Comment;
import ru.practicum.models.Event;
import ru.practicum.models.Request;
import ru.practicum.models.User;
import ru.practicum.repositories.CommentRepository;
import ru.practicum.repositories.EventRepository;
import ru.practicum.repositories.RequestRepository;
import ru.practicum.repositories.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CommentFullDto> getAllForUser(Long userId) {
        checkUser(userId);

        List<Comment> comments = commentRepository.findAllByAuthor_Id(userId);

        if (comments.isEmpty()) {
            return Collections.emptyList();
        }

        return comments
                .stream()
                .map(CommentMapper::toCommentFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getAllForEvent(Long eventId) {
        checkEvent(eventId);
        return commentRepository.findAllByEvent_Id(eventId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentFullDto postComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User user = checkUser(userId);
        Event event = checkEvent(eventId);

        Request request = requestRepository.findByEvent_IdAndRequester_Id(eventId, userId);

        if (!event.getInitiator().getId().equals(userId) && request == null) {
            throw new CommentPostDenyException(String.format("Попытка добавить комментарий к событию с id=%d," +
                    " без заявки на участие от пользователя с id=%d", eventId, userId));
        }

        if (event.getRequestModeration()) {
            if (request != null && request.getStatus() != RequestStatusEnum.CONFIRMED) {
                throw new CommentPostDenyException(String.format("Попытка добавить комментарий к событию с id=%d," +
                        " без одобренной заявки на участие от пользователя с id=%d", eventId, userId));
            }
        }

        Comment comment = CommentMapper.toComment(newCommentDto, user, event);

        return CommentMapper.toCommentFullDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentFullDto editComment(Long userId, Long eventId, Long commId, NewCommentDto newCommentDto) {
        checkUser(userId);
        checkEvent(eventId);
        Comment comment = checkComment(commId);

        if (comment.getStatus().equals(CommentStatusEnum.PUBLISHED)) {
            throw new ConflictException("Нельзя редактировать уже опубликованный комментарий.");
        }

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new CommentPostDenyException(String.format("Попытка отредактировать коментарий" +
                    " от пользователя с id=%d, который не является автором комментария.", userId));
        }

        comment.setText(newCommentDto.getText());
        comment.setStatus(CommentStatusEnum.PENDING);

        return CommentMapper.toCommentFullDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentDto increaseLikesForComment(Long userId, Long eventId, Long commId) {
        checkUser(userId);
        checkEvent(eventId);
        Comment comment = checkComment(commId);

        if (comment.getStatus() != CommentStatusEnum.PUBLISHED) {
            throw new ConflictException("Нельзя лайкнуть неопубликованный комментарий");
        }

        Integer likes = comment.getLikes();

        comment.setLikes(likes + 1);

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentFullDto> getCommentsForAdmin(Long eventId) {
        checkEvent(eventId);
        return commentRepository.findAllByEvent_Id(eventId)
                .stream()
                .map(CommentMapper::toCommentFullDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentFullDto editCommentByAdmin(Long eventId, Long commId, String actionAdmin) {
        checkEvent(eventId);
        Comment comment = checkComment(commId);
        StateActionAdminCommentEnum actionAdminCommentEnum = checkState(actionAdmin.toUpperCase());
        switch (actionAdminCommentEnum) {
            case REJECT_COMMENT:
                comment.setStatus(CommentStatusEnum.REJECTED);
                break;
            case PUBLISH_COMMENT:
                if (comment.getStatus() == CommentStatusEnum.PENDING) {
                    comment.setStatus(CommentStatusEnum.PUBLISHED);
                }
        }
        commentRepository.save(comment);
        return CommentMapper.toCommentFullDto(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long eventId, Long commId) {
        checkEvent(eventId);
        checkComment(commId);
        commentRepository.deleteById(commId);
    }

    private StateActionAdminCommentEnum checkState(String state) {
        try {
            return StateActionAdminCommentEnum.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException(
                        String.format("User with id=%d was not found", userId))
        );
    }

    private Event checkEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException(
                        String.format("Event with id=%d was not found", eventId))
        );
    }

    private Comment checkComment(Long commId) {
        return commentRepository.findById(commId).orElseThrow(
                () -> new ObjectNotFoundException(
                        String.format("Comment with id=%d was not found", commId))
        );
    }
}
