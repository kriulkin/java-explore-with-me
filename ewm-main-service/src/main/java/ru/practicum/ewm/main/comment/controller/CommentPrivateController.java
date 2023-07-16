package ru.practicum.ewm.main.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.comment.dto.CommentDto;
import ru.practicum.ewm.main.comment.dto.NewCommentDto;
import ru.practicum.ewm.main.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/user/{userId}/comments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CommentPrivateController {
    private final CommentService commentService;

    @PostMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@PositiveOrZero @PathVariable long userId,
                                 @PositiveOrZero @PathVariable long eventId,
                                 @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("Create comment for event with id = {} by user with id = {}", userId, eventId);
        return commentService.addComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@PositiveOrZero @PathVariable long userId,
                                    @PositiveOrZero @PathVariable long commentId,
                                    @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("Update comment with id = {}", commentId);
        return commentService.updateComment(userId, commentId, newCommentDto);
    }
}
