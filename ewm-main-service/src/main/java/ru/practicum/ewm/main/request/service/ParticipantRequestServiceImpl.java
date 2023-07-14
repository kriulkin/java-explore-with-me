package ru.practicum.ewm.main.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.event.model.EventState;
import ru.practicum.ewm.main.event.service.RequestStatus;
import ru.practicum.ewm.main.event.storage.EventStorage;
import ru.practicum.ewm.main.exception.ModificationForbiddenException;
import ru.practicum.ewm.main.exception.NoSuchEntityException;
import ru.practicum.ewm.main.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.main.request.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.main.request.model.ParticipationRequest;
import ru.practicum.ewm.main.request.storage.ParticipantRequestStorage;
import ru.practicum.ewm.main.user.model.User;
import ru.practicum.ewm.main.user.storage.UserStorage;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipantRequestServiceImpl implements ParticipantRequestService {
    private final ParticipantRequestStorage requestStorage;
    private final UserStorage userStorage;
    private final EventStorage eventStorage;

    @Override
    @Transactional
    public ParticipationRequestDto addRequest(long userId, long eventId) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NoSuchEntityException(
                String.format("No such user with id = %d", userId)));

        Event event = eventStorage.findById(eventId).orElseThrow(() -> new NoSuchEntityException(
                        String.format("No such event with id = %d", eventId)
                )
        );

        if (event.getInitiator().getId() == userId) {
            throw new ModificationForbiddenException("User cannot create request for this event");
        }

        if (event.getState() != (EventState.PUBLISHED)) {
            throw new ModificationForbiddenException("User cannot request participation in not published event");
        }

        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new ModificationForbiddenException("User cannot request participation in already started event");
        }

        int confirmedRequests = requestStorage.countByEvent_IdAndStatus(event.getId(), RequestStatus.CONFIRMED);

        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= confirmedRequests) {
            throw new ModificationForbiddenException("User cannot take a participation due to participation limit exceeded");
        }

        return ParticipationRequestMapper.toRequestDto(requestStorage.save(ParticipationRequestMapper.toRequest(
                user,
                event,
                !event.isRequestModeration() || event.getParticipantLimit() == 0 ? RequestStatus.CONFIRMED : RequestStatus.PENDING
        )));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        ParticipationRequest request = requestStorage.findByIdAndRequester_Id(requestId, userId)
                .orElseThrow(() -> new NoSuchEntityException(String.format("No such request with id = %d", requestId)));
        request.setStatus(RequestStatus.CANCELED);
        requestStorage.save(request);

        return ParticipationRequestMapper.toRequestDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(long userId) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NoSuchEntityException(
                String.format("No such user with id = %d", userId)));
        List<ParticipationRequest> requests = requestStorage.findByRequester(user);

        return ParticipationRequestMapper.toRequestDtoList(requests);
    }
}
