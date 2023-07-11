package ru.practicum.ewm.main.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.main.category.dto.CategoryDto;
import ru.practicum.ewm.main.event.model.EventState;
import ru.practicum.ewm.main.user.dto.UserShortDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class EventFullDto {
    @NonNull
    Long id;

    @NonNull
    String annotation;

    @NonNull
    CategoryDto category;

    int confirmedRequests;

    @NonNull
    String createdOn;

    @NonNull
    String description;

    @NonNull
    String eventDate;

    @NonNull
    UserShortDto initiator;

    @NonNull
    LocationDto location;

    @NonNull
    Boolean paid;

    @NonNull
    Integer participantLimit;

    @NonNull
    String publishedOn;

    @NonNull
    Boolean requestModeration;

    @NonNull
    EventState state;

    @NonNull
    String title;

    long views;
}
