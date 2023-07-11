package ru.practicum.ewm.main.event.service;

import ru.practicum.ewm.main.event.dto.*;
import ru.practicum.ewm.main.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    EventFullDto updateAdminEvent(Long eventId, UpdateEventAdminRequest request);

    List<EventFullDto> getEventsList(List<Long> users, List<String> states, List<Long> categories, String rangeStart,
                                     String rangeEnd, int from, int size);

    EventFullDto addEvent(long userId, NewEventDto newEventDto);

    EventFullDto getEvent(long userId, long eventId);

    List<EventShortDto> getUserEventList(long userId, int from, int size);

    EventFullDto updateUserEvent(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> getEventRequestList(long userId, long eventId);

    EventRequestStatusUpdateResult updateRequestStatus(long userId, long eventId, EventRequestStatusUpdateRequest request);

    EventFullDto getEvent(long eventId, HttpServletRequest request);

    List<EventFullDto> searchEvents(String text, Long[] categories, Boolean paid, String rangeStart, String rangeEnd, String sort, int from, int size, boolean onlyAvailable, HttpServletRequest httpServletRequest);
}
