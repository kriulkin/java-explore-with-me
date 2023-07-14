package ru.practicum.ewm.main.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.event.service.RequestStatus;
import ru.practicum.ewm.main.request.model.ParticipationRequest;
import ru.practicum.ewm.main.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ParticipantRequestStorage extends JpaRepository<ParticipationRequest, Long> {

    int countByEvent_IdAndStatus(Long id, RequestStatus requestStatus);

    Optional<ParticipationRequest> findByIdAndRequester_Id(long requestId, long userId);

    List<ParticipationRequest> findByRequester(User user);

    List<ParticipationRequest> findByEvent_IdInAndStatus(Set<Long> longs, RequestStatus requestStatus);

    int findByEventAndStatus(Event event, RequestStatus requestStatus);

    List<ParticipationRequest> findByEvent(Event event);

    List<ParticipationRequest> findByIdInAndEvent_Id(Set<Long> requestIds, long eventId);

    List<ParticipationRequest> findByEvent_IdAndStatus(long eventId, RequestStatus requestStatus);

    int countByEventAndStatus(Event event, RequestStatus requestStatus);
}
