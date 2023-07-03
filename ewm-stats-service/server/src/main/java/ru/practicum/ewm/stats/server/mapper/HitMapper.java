package ru.practicum.ewm.stats.server.mapper;

import ru.practicum.ewm.stats.dto.NewEndpointHit;
import ru.practicum.ewm.stats.server.model.EndpointHit;

public class HitMapper {
    public static EndpointHit toEndpointHit(NewEndpointHit hit) {
        return new EndpointHit(
                null,
                hit.getApp(),
                hit.getUri(),
                hit.getIp(),
                hit.getTimestamp()
        );
    }
}
