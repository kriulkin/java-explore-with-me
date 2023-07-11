package ru.practicum.ewm.main.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.event.dto.*;
import ru.practicum.ewm.main.event.service.EventService;
import ru.practicum.ewm.main.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventPrivateController {
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PositiveOrZero @PathVariable long userId,
                                 @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Create event: {}", newEventDto);
        return eventService.addEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEvent(@PositiveOrZero @PathVariable long userId,
                                 @PositiveOrZero @PathVariable long eventId) {
        log.info("Get event with id = {}", eventId);
        return eventService.getEvent(userId, eventId);
    }

    @GetMapping
    public List<EventShortDto> getUserEventList(@PositiveOrZero @PathVariable long userId,
                                                @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Get event list for user with id = {}", userId);
        return eventService.getUserEventList(userId, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PositiveOrZero @PathVariable long userId,
                                    @PositiveOrZero @PathVariable long eventId,
                                    @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("Update event with id = {} by user with id = {}", eventId, userId);
        return eventService.updateUserEvent(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequestList(@PositiveOrZero @PathVariable long userId,
                                                             @PositiveOrZero @PathVariable long eventId) {
        log.info("Get participation request list for event with id = {}", eventId);
        return eventService.getEventRequestList(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatus(@PositiveOrZero @PathVariable long userId,
                                                              @PositiveOrZero @PathVariable long eventId,
                                                              @Valid @RequestBody EventRequestStatusUpdateRequest request) {
        log.info("Update event participation request statuses for event with id = {}", eventId);
        return eventService.updateRequestStatus(userId, eventId, request);
    }
}
