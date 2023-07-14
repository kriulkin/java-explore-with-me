package ru.practicum.ewm.main.request.mapper;

import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.event.service.RequestStatus;
import ru.practicum.ewm.main.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.main.request.model.ParticipationRequest;
import ru.practicum.ewm.main.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ParticipationRequestMapper {
    public static ParticipationRequestDto toRequestDto(ParticipationRequest participationRequest) {
        return new ParticipationRequestDto(
                participationRequest.getId(),
                participationRequest.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                participationRequest.getEvent().getId(),
                participationRequest.getRequester().getId(),
                participationRequest.getStatus()
        );
    }

    public static ParticipationRequest toRequest(User requester, Event event, RequestStatus status) {
        return new ParticipationRequest(
                requester,
                event,
                LocalDateTime.now(),
                status);
    }

    public static List<ParticipationRequestDto> toRequestDtoList(List<ParticipationRequest> requests) {
        return requests.stream().map(ParticipationRequestMapper::toRequestDto).collect(Collectors.toList());
    }
}
