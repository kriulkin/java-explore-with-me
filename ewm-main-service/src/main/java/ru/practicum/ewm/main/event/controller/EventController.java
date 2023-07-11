package ru.practicum.ewm.main.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.event.dto.EventFullDto;
import ru.practicum.ewm.main.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventController {
    private final EventService eventService;

    @GetMapping("/{eventId}")
    public EventFullDto getEvent(@PositiveOrZero @PathVariable long eventId,
                                 HttpServletRequest request) {
        log.info("Get event with id = {}", eventId);
        return eventService.getEvent(eventId, request);
    }

    @GetMapping
    public List<EventFullDto> searchEvents(@RequestParam(required = false) String text,
                                           @RequestParam(required = false) Long[] categories,
                                           @RequestParam(required = false) Boolean paid,
                                           @RequestParam(required = false) String rangeStart,
                                           @RequestParam(required = false) String rangeEnd,
                                           @RequestParam(required = false) String sort,
                                           @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                           @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                           @Positive @RequestParam(defaultValue = "10") @Positive int size,
                                           HttpServletRequest httpServletRequest) {
        log.info("Search events");
        return eventService.searchEvents(text, categories, paid, rangeStart, rangeEnd, sort, from, size, onlyAvailable,
                httpServletRequest);
    }
}
