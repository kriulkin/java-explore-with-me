package ru.practicum.ewm.main.event.mapper;

import ru.practicum.ewm.main.category.mapper.CategoryMapper;
import ru.practicum.ewm.main.category.model.Category;
import ru.practicum.ewm.main.comment.mapper.CommentMapper;
import ru.practicum.ewm.main.event.dto.EventFullDto;
import ru.practicum.ewm.main.event.dto.EventShortDto;
import ru.practicum.ewm.main.event.dto.NewEventDto;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.event.model.EventState;
import ru.practicum.ewm.main.event.model.Location;
import ru.practicum.ewm.main.user.mapper.UserMapper;
import ru.practicum.ewm.main.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


public class EventMapper {
    private EventMapper() {
    }

    public static Event toEvent(NewEventDto newEventDto, User initiator, Category category, Location location) {
        return new Event(
                newEventDto.getAnnotation(),
                category,
                LocalDateTime.now(),
                newEventDto.getDescription(),
                newEventDto.getEventDate(),
                initiator,
                location,
                newEventDto.getPaid() == null ? false : newEventDto.getPaid(),
                newEventDto.getParticipantLimit() == null ? 0 : newEventDto.getParticipantLimit(),
                newEventDto.getRequestModeration() == null ? true : newEventDto.getRequestModeration(),
                EventState.PENDING,
                newEventDto.getTitle()
        );
    }

    public static EventFullDto toEventFullDto(Event event) {
        return new EventFullDto(
                event.getId(),
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                event.getCreatedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                event.getDescription(),
                event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                UserMapper.toUserShortDto(event.getInitiator()),
                LocationMapper.toLocationDto(event.getLocation()),
                event.isPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn() == null ? null : event.getPublishedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                event.isRequestModeration(),
                event.getState(),
                event.getTitle(),
                CommentMapper.toCommentDtoList(event.getComments())
        );
    }

    public static EventShortDto toEventShortDto(Event event) {
        return new EventShortDto(
                event.getId(),
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.isPaid(),
                event.getTitle(),
                (long) event.getComments().size()
        );
    }

    public static List<EventShortDto> toListEventShortDto(List<Event> events) {
        return events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    public static List<EventFullDto> toListEventFullDto(List<Event> events) {
        return events.stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
    }
}
