package ru.practicum.ewm.main.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.comment.dto.CommentDto;
import ru.practicum.ewm.main.comment.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CommentAdminController {
    private final CommentService commentService;

    @GetMapping("/{commentId}")
    public CommentDto getComment(@PositiveOrZero @PathVariable long commentId) {
        log.info("Get comment with id = {}", commentId);
        return commentService.getComment(commentId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PositiveOrZero @PathVariable long commentId) {
        log.info("Delete comment with id = {}", commentId);
        commentService.deleteComment(commentId);
    }

    @GetMapping("/users/{userId}")
    public List<CommentDto> getUserComments(@PositiveOrZero @PathVariable long userId,
                                            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                            @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Get comments owned by user with id = {}", userId);
        return commentService.getUserComments(userId, from, size);
    }

    @GetMapping("/events/{eventId}")
    public List<CommentDto> getEventComments(@PositiveOrZero @PathVariable long eventId) {
        log.info("Get comments owned by user with id = {}", eventId);
        return commentService.getEventComments(eventId);
    }

    @GetMapping
    public List<CommentDto> getAllComments() {
        log.info("Get all comments");
        return commentService.getAllComments();
    }
}
