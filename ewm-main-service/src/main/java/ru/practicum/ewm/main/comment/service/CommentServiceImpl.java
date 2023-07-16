package ru.practicum.ewm.main.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.comment.dto.CommentDto;
import ru.practicum.ewm.main.comment.dto.NewCommentDto;
import ru.practicum.ewm.main.comment.mapper.CommentMapper;
import ru.practicum.ewm.main.comment.model.Comment;
import ru.practicum.ewm.main.comment.storage.CommentStorage;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.event.storage.EventStorage;
import ru.practicum.ewm.main.exception.ModificationForbiddenException;
import ru.practicum.ewm.main.exception.NoSuchEntityException;
import ru.practicum.ewm.main.user.model.User;
import ru.practicum.ewm.main.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentStorage commentStorage;
    private final UserStorage userStorage;
    private final EventStorage eventStorage;


    @Override
    public CommentDto addComment(long userId, long eventId, NewCommentDto newCommentDto) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NoSuchEntityException(
                String.format("No such user with id = %d", userId)));

        Event event = eventStorage.findById(eventId).orElseThrow(() -> new NoSuchEntityException(
                        String.format("No such event with id = %d", eventId)
                )
        );

        if (commentStorage.existsByUserAndEvent(user, event)) {
            throw new ModificationForbiddenException("User already commented this event");
        }

        Comment comment = commentStorage.save(CommentMapper.toComment(newCommentDto, user, event));

        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public CommentDto updateComment(long userId, long commentId, NewCommentDto newCommentDto) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NoSuchEntityException(
                String.format("No such user with id = %d", userId)));

        Comment comment = commentStorage.findById(commentId).orElseThrow(() -> new NoSuchEntityException(
                String.format("No such comment with id = %d", commentId)));

        if (userId != comment.getUser().getId()) {
            throw new ModificationForbiddenException(
                    String.format("User has no access to edit comment with id = %d", commentId));
        }

        Comment newComment = commentStorage.save(CommentMapper.toComment(newCommentDto, user, comment.getEvent()));
        return CommentMapper.toCommentDto(newComment);
    }

    @Override
    public void deleteComment(long commentId) {
        commentStorage.findById(commentId).orElseThrow(() -> new NoSuchEntityException(
                String.format("No such comment with id = %d", commentId)));

        commentStorage.deleteById(commentId);
    }

    @Override
    public List<CommentDto> getUserComments(long userId, int from, int size) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NoSuchEntityException(
                String.format("No such user with id = %d", userId)));

        Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));

        List<Comment> comments = commentStorage.findByUser(user, page);
        return CommentMapper.toCommentDtoList(comments);
    }

    @Override
    public List<CommentDto> getEventComments(long eventId) {
        Event event = eventStorage.findById(eventId).orElseThrow(() -> new NoSuchEntityException(
                        String.format("No such event with id = %d", eventId)
                )
        );

        List<Comment> comments = commentStorage.findByEvent(event);
        return CommentMapper.toCommentDtoList(comments);
    }

    @Override
    public List<CommentDto> getAllComments() {
        List<Comment> comments = commentStorage.findAll();
        return CommentMapper.toCommentDtoList(comments);
    }

    @Override
    public CommentDto getComment(long commentId) {
        Comment comment = commentStorage.findById(commentId).orElseThrow(() -> new NoSuchEntityException(
                String.format("No such comment with id = %d", commentId)));
        return CommentMapper.toCommentDto(comment);
    }
}
