package ru.practicum.ewm.main.request.service;

import ru.practicum.ewm.main.request.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipantRequestService {
    ParticipationRequestDto addRequest(long userId, long eventId);

    ParticipationRequestDto cancelRequest(long userId, long requestId);

    List<ParticipationRequestDto> getUserRequests(long userId);
}
