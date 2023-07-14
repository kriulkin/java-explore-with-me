package ru.practicum.ewm.main.comment.service;

import ru.practicum.ewm.main.comment.dto.CommentDto;
import ru.practicum.ewm.main.comment.dto.NewCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto addComment(long userId, long eventId, NewCommentDto commentDto);

    CommentDto updateComment(long userId, long commentId, NewCommentDto newCommentDto);

    void deleteComment(long commentId);

    List<CommentDto> getUserComments(long userId, int from, int size);

    List<CommentDto> getEventComments(long eventId);

    List<CommentDto> getAllComments();

    CommentDto getComment(long commentId);
}
