package ru.practicum.ewm.main.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.category.model.Category;
import ru.practicum.ewm.main.category.storage.CategoryStorage;
import ru.practicum.ewm.main.event.dto.*;
import ru.practicum.ewm.main.event.mapper.EventMapper;
import ru.practicum.ewm.main.event.mapper.LocationMapper;
import ru.practicum.ewm.main.event.model.*;
import ru.practicum.ewm.main.event.storage.EventStorage;
import ru.practicum.ewm.main.event.storage.LocationStorage;
import ru.practicum.ewm.main.exception.ArgumentValidationException;
import ru.practicum.ewm.main.exception.ModificationForbiddenException;
import ru.practicum.ewm.main.exception.NoSuchEntityException;
import ru.practicum.ewm.main.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.main.request.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.main.request.model.ParticipationRequest;
import ru.practicum.ewm.main.request.storage.ParticipantRequestStorage;
import ru.practicum.ewm.main.user.model.User;
import ru.practicum.ewm.main.user.storage.UserStorage;
import ru.practicum.ewm.stats.client.StatsClient;
import ru.practicum.ewm.stats.dto.NewEndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final UserStorage userStorage;
    private final CategoryStorage categoryStorage;
    private final EventStorage eventStorage;
    private final StatsClient statsClient;
    private final ParticipantRequestStorage requestStorage;
    private final LocationStorage locationStorage;

    @Override
    @Transactional
    public EventFullDto updateAdminEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventStorage.findById(eventId).orElseThrow(() -> new NoSuchEntityException(
                        String.format("No such event with id = %d", eventId)
                )
        );

        if (updateEventAdminRequest.getEventDate() != null && !updateEventAdminRequest.getEventDate().isBlank()) {
            LocalDateTime newEventDate = LocalDateTime.parse(updateEventAdminRequest.getEventDate(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            if (newEventDate.isBefore(LocalDateTime.now().minusHours(1))) {
                throw new ArgumentValidationException("New event date in past");
            }

            event.setEventDate(newEventDate);
        }

        if (updateEventAdminRequest.getStateAction() == AdminStateAction.PUBLISH_EVENT
                && event.getState() == EventState.PUBLISHED) {
            throw new ModificationForbiddenException("Event already published");
        }

        if (updateEventAdminRequest.getStateAction() == AdminStateAction.PUBLISH_EVENT
                && event.getState() == EventState.CANCELED) {
            throw new ModificationForbiddenException("Event already canceled");
        }

        if (updateEventAdminRequest.getStateAction() == AdminStateAction.REJECT_EVENT
                && event.getState() == EventState.PUBLISHED) {
            throw new ModificationForbiddenException("Event already published");
        }

        if (updateEventAdminRequest.getStateAction() != null) {
            if (updateEventAdminRequest.getStateAction() == AdminStateAction.PUBLISH_EVENT) {
                event.setState(EventState.PUBLISHED);
            } else {
                event.setState(EventState.CANCELED);
            }
        }

        if (updateEventAdminRequest.getAnnotation() != null && !updateEventAdminRequest.getAnnotation().isBlank()) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }

        if (updateEventAdminRequest.getDescription() != null && !updateEventAdminRequest.getDescription().isBlank()) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }

        if (updateEventAdminRequest.getCategory() != null) {
            event.setCategory(categoryStorage.findById(updateEventAdminRequest.getCategory()).orElseThrow(
                            () -> new NoSuchEntityException("No such category")
                    )
            );
        }

        if (updateEventAdminRequest.getTitle() != null && !updateEventAdminRequest.getTitle().isBlank()) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }

        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }

        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }

        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }

        return EventMapper.toEventFullDto(eventStorage.save(event));
    }

    @Override
    public List<EventFullDto> getEventsList(List<Long> users, List<String> states, List<Long> categories,
                                            String rangeStart, String rangeEnd, int from, int size) {

        Map<String, LocalDateTime> rangeMap = prepareDateFields(rangeStart, rangeEnd);
        LocalDateTime startDate = rangeMap.get("startDate");
        LocalDateTime endDate = rangeMap.get("endDate");

        Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));

        QEvent eventQuery = QEvent.event;
        List<BooleanExpression> options = new ArrayList<>();
        if (users != null) {
            options.add(eventQuery.initiator.id.in(users));
        }
        if (states != null) {
            options.add(eventQuery.state.in(states.stream()
                    .map(EventState::valueOf).collect(Collectors.toList())));
        }
        if (categories != null) {
            options.add(eventQuery.category.id.in(categories));
        }

        getTimeOptions(startDate, endDate, eventQuery, options);

        BooleanExpression expression = options.stream()
                .reduce(BooleanExpression::and)
                .get();

        List<Event> events = eventStorage.findAll(expression, page).toList();
        List<EventFullDto> eventFullDtoList = EventMapper.toListEventFullDto(events);

        Map<Long, EventFullDto> eventFullDtoMap = eventFullDtoList.stream()
                .collect(Collectors.toMap(EventFullDto::getId, Function.identity()));
        List<ParticipationRequest> requests = requestStorage.findByEvent_IdInAndStatus(eventFullDtoMap.keySet(),
                RequestStatus.CONFIRMED);

        for (ParticipationRequest request : requests) {
            EventFullDto eventFullDto = eventFullDtoMap.get(request.getEvent().getId());
            eventFullDto.setConfirmedRequests(eventFullDto.getConfirmedRequests() + 1);
        }

        Map<String, Long> uris = new HashMap<>();
        eventFullDtoMap.keySet().forEach(id -> uris.put(String.format("/events/%d", id), id));

        List<ViewStats> stats = statsClient.getStats(startDate, endDate, uris.keySet().toArray(new String[0]), true);

        for (ViewStats stat : stats) {
            if (uris.containsKey(stat.getUri())) {
                long eventId = uris.get(stat.getUri());
                eventFullDtoList.get((int) eventId).setViews(stat.getHits());
            }
        }
        return List.copyOf(eventFullDtoMap.values());
    }

    @Override
    @Transactional
    public EventFullDto addEvent(long userId, NewEventDto newEventDto) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NoSuchEntityException(
                String.format("No such user with id = %d", userId)));

        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().minusHours(1))) {
            throw new ArgumentValidationException("New event date in past");
        }

        Category category = categoryStorage.findById(newEventDto.getCategory()).orElseThrow(
                () -> new NoSuchEntityException(String.format("No such category with id = %s", newEventDto.getCategory()))
        );

        Location location = locationStorage.save(newEventDto.getLocation());
        Event event = eventStorage.save(EventMapper.toEvent(newEventDto, user, category, location));
        return EventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto getEvent(long userId, long eventId) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NoSuchEntityException(
                String.format("No such user with id = %d", userId)));

        Event event = eventStorage.findByIdAndInitiator(eventId, user).orElseThrow(
                () -> new NoSuchEntityException(String.format("No such event with id = %d", eventId))
        );

        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        eventFullDto.setConfirmedRequests(requestStorage.countByEventAndStatus(event, RequestStatus.CONFIRMED));
        eventFullDto.setViews(statsClient.getStats(
                event.getCreatedOn(),
                event.getEventDate(),
                new String[]{String.format("/events/%d", eventId)},
                true
        ).size());

        return eventFullDto;
    }

    @Override
    @Transactional
    public List<EventShortDto> getUserEventList(long userId, int from, int size) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NoSuchEntityException(
                String.format("No such user with id = %d", userId)));

        Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        List<Event> events = eventStorage.findByInitiator(user, page).getContent();

        return EventMapper.toListEventShortDto(events);
    }

    @Override
    @Transactional
    public EventFullDto updateUserEvent(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NoSuchEntityException(
                String.format("No such user with id = %d", userId)));

        if (updateEventUserRequest.getEventDate() != null && !updateEventUserRequest.getEventDate().isBlank()) {
            LocalDateTime newEventDate = LocalDateTime.parse(updateEventUserRequest.getEventDate(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            if (newEventDate.isBefore(LocalDateTime.now().minusHours(1))) {
                throw new ArgumentValidationException("New event date in past");
            }
        }

        Event event = eventStorage.findByIdAndInitiator(eventId, user).orElseThrow(
                () -> new NoSuchEntityException(String.format("No such event with id = %d", eventId))
        );

        if (event.getState() == EventState.PUBLISHED) {
            throw new ModificationForbiddenException(String.format("Event with id = %d already published", eventId));
        }

        updateEvent(updateEventUserRequest, event);

        if (updateEventUserRequest.getStateAction() != null) {
            if (updateEventUserRequest.getStateAction() == StateAction.SEND_TO_REVIEW) {
                event.setState(EventState.PENDING);
            } else {
                event.setState(EventState.CANCELED);
            }
        }

        return EventMapper.toEventFullDto(eventStorage.save(event));
    }

    @Override
    @Transactional
    public List<ParticipationRequestDto> getEventRequestList(long userId, long eventId) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NoSuchEntityException(
                String.format("No such user with id = %d", userId)));

        Event event = eventStorage.findByIdAndInitiator(eventId, user).orElseThrow(
                () -> new ModificationForbiddenException(String.format("User with id = %d access denied", userId))
        );

        return ParticipationRequestMapper.toRequestDtoList(requestStorage.findByEvent(event));
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestStatus(long userId, long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NoSuchEntityException(
                String.format("No such user with id = %d", userId)));

        Event event = eventStorage.findByIdAndInitiator(eventId, user).orElseThrow(
                () -> new ModificationForbiddenException(String.format("User with id = %d access denied", userId))
        );

        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ModificationForbiddenException(String.format(
                    "Event with id = %d not required in moderation approving",
                    eventId)
            );
        }

        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new ModificationForbiddenException(String.format(
                    "Event with id = %d already started",
                    eventId)
            );
        }

        if (event.getParticipantLimit() <= requestStorage.countByEvent_IdAndStatus(eventId, RequestStatus.CONFIRMED)) {
            throw new ModificationForbiddenException(String.format(
                    "Event with id = %d exceeded moderation request limit",
                    eventId)
            );
        }

        List<ParticipationRequest> requests = requestStorage.findByIdInAndEvent_Id(eventRequestStatusUpdateRequest.getRequestIds(), eventId);

        if (requests.size() < eventRequestStatusUpdateRequest.getRequestIds().size()) {
            throw new NoSuchEntityException("Some requests have not found");
        }

        if (eventRequestStatusUpdateRequest.getStatus() == RequestStatus.CONFIRMED || eventRequestStatusUpdateRequest.getStatus() == RequestStatus.REJECTED) {
            requests.forEach(request -> request.setStatus(eventRequestStatusUpdateRequest.getStatus()));
        } else {
            throw new ModificationForbiddenException("Update status request is denied");
        }

        requestStorage.saveAll(requests);

        return new EventRequestStatusUpdateResult(
                ParticipationRequestMapper.toRequestDtoList(requestStorage.findByEvent_IdAndStatus(eventId, RequestStatus.CONFIRMED)),
                ParticipationRequestMapper.toRequestDtoList(requestStorage.findByEvent_IdAndStatus(eventId, RequestStatus.REJECTED))
        );
    }

    @Override
    @Transactional
    public EventFullDto getEvent(long eventId, HttpServletRequest httpServletRequest) {
        Event event = eventStorage.findById(eventId).orElseThrow(
                () -> new NoSuchEntityException(String.format("No such event with id = %d", eventId))
        );

        if (event.getState() != EventState.PUBLISHED) {
            throw new NoSuchEntityException(String.format("No such event with id = %d", eventId));
        }

        saveStats(httpServletRequest);

        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        List<ViewStats> stats = statsClient.getStats(event.getCreatedOn(),
                LocalDateTime.now(),
                new String[]{String.format("/events/%d", eventId)},
                true
        );

        if (!stats.isEmpty()) {
            eventFullDto.setViews(stats.get(0).getHits());
        }
        return eventFullDto;
    }

    @Override
    @Transactional
    public List<EventFullDto> searchEvents(
            String text,
            Long[] categories,
            Boolean paid,
            String rangeStart,
            String rangeEnd,
            String sort,
            int from,
            int size,
            boolean onlyAvailable,
            HttpServletRequest httpServletRequest) {

        Map<String, LocalDateTime> rangeMap = prepareDateFields(rangeStart, rangeEnd);
        LocalDateTime startDate = rangeMap.get("startDate");
        LocalDateTime endDate = rangeMap.get("endDate");

        Sort sortOrder = Sort.unsorted();
        if (sort != null) {
            if (sort.equals("EVENT_DATE")) {
                sortOrder = Sort.by("eventDate");
            }
            if (sort.equals("VIEWS")) {
                sortOrder = Sort.by("views");
            }
        }

        QEvent event = QEvent.event;
        List<BooleanExpression> options = new ArrayList<>();
        if (text != null) {
            options.add(event.annotation.like(text).or(event.description.like(text)));
        }
        if (categories != null) {
            options.add(event.category.id.in(categories));
        }
        if (paid != null) {
            options.add(event.paid.eq(paid));
        }

        getTimeOptions(startDate, endDate, event, options);
        BooleanExpression expression = options.stream()
                .reduce(BooleanExpression::and)
                .get();
        List<Event> events = eventStorage.findAll(expression, PageRequest.of(from / size, size, sortOrder)).toList();
        List<EventFullDto> eventFullDtoList = EventMapper.toListEventFullDto(events);

        Map<Long, EventFullDto> eventFullDtoMap = eventFullDtoList.stream()
                .collect(Collectors.toMap(EventFullDto::getId, Function.identity()));
        List<ParticipationRequest> requests = requestStorage.findByEvent_IdInAndStatus(eventFullDtoMap.keySet(),
                RequestStatus.CONFIRMED);

        for (ParticipationRequest request : requests) {
            EventFullDto eventFullDto = eventFullDtoMap.get(request.getEvent().getId());
            eventFullDto.setConfirmedRequests(eventFullDto.getConfirmedRequests() + 1);
        }

        Map<String, Long> uris = new HashMap<>();
        eventFullDtoMap.keySet().forEach(id -> uris.put(String.format("/events/%d", id), id));

        List<ViewStats> stats = statsClient.getStats(startDate, endDate, uris.keySet().toArray(new String[0]), true);

        for (ViewStats stat : stats) {
            if (uris.containsKey(stat.getUri())) {
                long eventId = uris.get(stat.getUri());
                eventFullDtoMap.get(eventId).setViews((stat.getHits()));
            }
        }

        if (onlyAvailable) {
            eventFullDtoList = eventFullDtoList.stream()
                    .filter(e -> e.getConfirmedRequests() < e.getParticipantLimit())
                    .collect(Collectors.toList());
        }

        saveStats(httpServletRequest);
        return eventFullDtoList;
    }

    private void saveStats(HttpServletRequest httpServletRequest) {
        NewEndpointHit newEndpointHit = new NewEndpointHit(
                "ewm-main-service",
                httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr(),
                LocalDateTime.now()
        );

        statsClient.addHit(newEndpointHit);
    }

    private void getTimeOptions(LocalDateTime start, LocalDateTime end, QEvent event, List<BooleanExpression> options) {
        if (start != null) {
            options.add(event.eventDate.after(start));
        }
        if (end != null) {
            options.add(event.eventDate.before(end));
        }
        if (start == null && end == null) {
            options.add(event.eventDate.after(LocalDateTime.now()));
        }
        if (start != null && end != null && start.isAfter(end)) {
            throw new ArgumentValidationException("Start date after end date");
        }
    }

    private void updateEvent(UpdateEventUserRequest updateEventUserRequest, Event event) {
        updateEventCommon(event,
                updateEventUserRequest.getAnnotation(),
                updateEventUserRequest.getCategory(),
                updateEventUserRequest.getDescription(),
                updateEventUserRequest.getEventDate(),
                updateEventUserRequest.getLocation(),
                updateEventUserRequest.getPaid(),
                updateEventUserRequest.getParticipantLimit(),
                updateEventUserRequest.getRequestModeration(),
                updateEventUserRequest.getTitle());
    }

    private void updateEventCommon(Event event, String annotation, Long category, String description, String eventDate, LocationDto location, Boolean paid, Integer participantLimit, Boolean requestModeration, String title) {
        if (annotation != null && annotation.isBlank()) {
            event.setAnnotation(annotation);
        }

        if (category != null) {
            event.setCategory(categoryStorage.findById(category).orElseThrow(
                    () -> new NoSuchEntityException(String.format("No such category with id = %d", category))
            ));
        }

        if (description != null && description.isBlank()) {
            event.setDescription(description);
        }

        if (eventDate != null && eventDate.isBlank()) {
            event.setEventDate(LocalDateTime.parse(eventDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        if (location != null) {
            long locationId = event.getLocation().getId();
            Location newLocation = locationStorage.save(LocationMapper.toLocation(location));
            event.setLocation(newLocation);
            locationStorage.deleteById(locationId);
        }

        if (paid != null) {
            event.setPaid(paid);
        }

        if (participantLimit != null) {
            event.setParticipantLimit(participantLimit);
        }

        if (requestModeration != null) {
            event.setRequestModeration(requestModeration);
        }

        if (title != null && title.isBlank()) {
            event.setTitle(title);
        }
    }

    public Map<String, LocalDateTime> prepareDateFields(String rangeStart, String rangeEnd) {
        LocalDateTime startDate;
        LocalDateTime endDate;

        if (rangeStart == null || rangeStart.isBlank()) {
            startDate = LocalDateTime.now().minusYears(200L);
        } else {
            try {
                startDate = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (DateTimeParseException e) {
                throw new ArgumentValidationException("The date field invalid");
            }
        }

        if (rangeEnd == null || rangeEnd.isBlank()) {
            endDate = LocalDateTime.now().plusYears(200L);
        } else {
            try {
                endDate = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (DateTimeParseException e) {
                throw new ArgumentValidationException("The date field invalid");
            }
        }

        if (startDate.isAfter(endDate)) {
            throw new ArgumentValidationException("Range start after Range end");
        }

        return Map.of(
                "startDate", startDate,
                "endDate", endDate
        );
    }
}
