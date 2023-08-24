package ru.practicum.errorHandler.exceptions;

public class CommentPostDenyException extends RuntimeException {
    public CommentPostDenyException(String message) {
        super(message);
    }
}
