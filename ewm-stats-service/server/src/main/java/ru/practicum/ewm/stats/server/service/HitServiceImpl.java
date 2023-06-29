package ru.practicum.ewm.stats.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.dto.NewEndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;
import ru.practicum.ewm.stats.server.mapper.HitMapper;
import ru.practicum.ewm.stats.server.model.EndpointHit;
import ru.practicum.ewm.stats.server.storage.HitStorage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HitServiceImpl implements HitService {
    private final HitStorage hitStorage;

    public EndpointHit addHit(NewEndpointHit hit) {
        return hitStorage.save(HitMapper.toEndpointHit(hit));
    }

    @Override
    public List<ViewStats> getStats(String start, String end, String[] uris, boolean unique) {
        LocalDateTime startDate = LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endDate = LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if (unique) {
            return hitStorage.getStatsWithUniqHits(startDate, endDate, uris);
        }

        if (uris == null) {
            return hitStorage.getStatsWithoutUris(startDate, endDate);
        }

        return hitStorage.getStats(startDate, endDate, uris);
    }
}
