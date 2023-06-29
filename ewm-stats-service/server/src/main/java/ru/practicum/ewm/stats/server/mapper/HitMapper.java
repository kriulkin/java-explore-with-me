package ru.practicum.ewm.stats.server.mapper;

import ru.practicum.ewm.stats.dto.NewEndpointHit;
import ru.practicum.ewm.stats.server.model.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HitMapper {
    public static EndpointHit toEndpointHit(NewEndpointHit hit) {
        return new EndpointHit(
                null,
                hit.getApp(),
                hit.getUri(),
                hit.getIp(),
                LocalDateTime.parse(hit.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }
}
