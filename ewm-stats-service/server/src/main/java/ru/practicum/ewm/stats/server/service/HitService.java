package ru.practicum.ewm.stats.server.service;

import ru.practicum.ewm.stats.dto.NewEndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;
import ru.practicum.ewm.stats.server.model.EndpointHit;

import java.util.List;

public interface HitService {
    EndpointHit addHit(NewEndpointHit hit);

    List<ViewStats> getStats(String start, String end, String[] uris, boolean unique);
}
