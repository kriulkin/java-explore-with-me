package ru.practicum.ewm.main.comment.mapper;

import ru.practicum.ewm.main.comment.dto.CommentDto;
import ru.practicum.ewm.main.comment.dto.NewCommentDto;
import ru.practicum.ewm.main.comment.model.Comment;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getUser().getName(),
                comment.getEvent().getId(),
                comment.getCreated()
        );
    }

    public static Comment toComment(NewCommentDto newCommentDto, User user, Event event) {
        return new Comment(
                newCommentDto.getText(),
                event,
                user,
                LocalDateTime.now()
        );
    }

    public static List<CommentDto> toCommentDtoList(List<Comment> comments) {
        return comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }
}
