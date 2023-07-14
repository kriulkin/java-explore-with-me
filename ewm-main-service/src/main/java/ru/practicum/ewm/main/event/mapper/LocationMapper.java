package ru.practicum.ewm.main.event.mapper;

import ru.practicum.ewm.main.event.dto.LocationDto;
import ru.practicum.ewm.main.event.model.Location;

public class LocationMapper {
    private LocationMapper() {
    }

    public static Location toLocation(LocationDto locationDto) {
        return new Location(locationDto.getLat(), locationDto.getLon());
    }

    public static LocationDto toLocationDto(Location location) {
        return new LocationDto(location.getLat(), location.getLon());
    }
}
