package ru.practicum.ewm.stats.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.dto.NewEndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;
import ru.practicum.ewm.stats.server.exception.ArgumentValidationException;
import ru.practicum.ewm.stats.server.mapper.HitMapper;
import ru.practicum.ewm.stats.server.model.EndpointHit;
import ru.practicum.ewm.stats.server.storage.HitStorage;

import javax.transaction.Transactional;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HitServiceImpl implements HitService {
    private final HitStorage hitStorage;

    @Transactional
    @Override
    public EndpointHit addHit(NewEndpointHit hit) {
        return hitStorage.save(HitMapper.toEndpointHit(hit));
    }

    @Transactional
    @Override
    public List<ViewStats> getStats(String start, String end, String[] uris, boolean unique) {
        LocalDateTime startDate = LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endDate = LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if (startDate.isAfter(endDate)) {
            throw new ArgumentValidationException("Дата начала позже даты завершения периода");
        }

        if (unique) {
            return hitStorage.getStatsWithUniqHits(startDate, endDate, uris);
        }

        if (uris == null) {
            return hitStorage.getStatsWithoutUris(startDate, endDate);
        }

        return hitStorage.getStats(startDate, endDate, uris);
    }
}
