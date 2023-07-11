package ru.practicum.ewm.main.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.main.request.service.ParticipantRequestService;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ParticipantRequestController {
    private final ParticipantRequestService participantRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PositiveOrZero @PathVariable long userId,
                                              @PositiveOrZero @RequestParam long eventId) {
        log.info("Create request by user with id {} for event with id {}", userId, eventId);
        return participantRequestService.addRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")

    public ParticipationRequestDto cancelRequest(@PositiveOrZero @PathVariable long userId,
                                                 @PositiveOrZero @PathVariable long requestId) {
        log.info("Cancel request with id = {} by user with id = {}", requestId, userId);
        return participantRequestService.cancelRequest(userId, requestId);
    }

    @GetMapping
    public List<ParticipationRequestDto> getUserRequests(@PositiveOrZero @PathVariable long userId) {
        log.info("Get requests list for user with id = {}", userId);
        return participantRequestService.getUserRequests(userId);
    }
}
