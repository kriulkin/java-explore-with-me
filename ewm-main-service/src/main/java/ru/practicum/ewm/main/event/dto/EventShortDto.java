package ru.practicum.ewm.main.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.main.category.dto.CategoryDto;
import ru.practicum.ewm.main.user.dto.UserShortDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortDto {
    @NonNull
    Long id;

    @NonNull
    String annotation;

    @NonNull
    CategoryDto category;

    @NonNull
    String eventDate;

    @NonNull
    UserShortDto initiator;

    @NonNull
    Boolean paid;

    @NonNull
    String title;
}
