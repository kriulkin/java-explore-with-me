package ru.practicum.ewm.main.event.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.main.category.dto.CategoryDto;
import ru.practicum.ewm.main.comment.dto.CommentDto;
import ru.practicum.ewm.main.event.model.EventState;
import ru.practicum.ewm.main.user.dto.UserShortDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {
    Long id;
    String annotation;
    CategoryDto category;
    int confirmedRequests;
    String createdOn;
    String description;
    String eventDate;
    UserShortDto initiator;
    LocationDto location;
    Boolean paid;
    Integer participantLimit;
    String publishedOn;
    Boolean requestModeration;
    EventState state;
    String title;
    long views;
    List<CommentDto> comments;

    public EventFullDto(Long id,
                        String annotation,
                        CategoryDto category,
                        String createdOn,
                        String description,
                        String eventDate,
                        UserShortDto initiator,
                        LocationDto location,
                        Boolean paid,
                        Integer participantLimit,
                        String publishedOn,
                        Boolean requestModeration,
                        EventState state,
                        String title,
                        List<CommentDto> comments
    ) {
        this.id = id;
        this.annotation = annotation;
        this.category = category;
        this.createdOn = createdOn;
        this.description = description;
        this.eventDate = eventDate;
        this.initiator = initiator;
        this.location = location;
        this.paid = paid;
        this.participantLimit = participantLimit;
        this.publishedOn = publishedOn;
        this.requestModeration = requestModeration;
        this.state = state;
        this.title = title;
        this.comments = comments;
    }
}
