package ru.practicum.ewm.stats.server.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.stats.dto.ViewStats;
import ru.practicum.ewm.stats.server.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface HitStorage extends JpaRepository<EndpointHit, Long> {
    @Query("select new ru.practicum.ewm.stats.dto.ViewStats(app, uri, count(distinct ip)) from EndpointHit " +
            "where timestamp between :startDate and :endDate " +
            "and uri in :uris " +
            "group by app, uri " +
            "order by count(distinct ip) desc")
    List<ViewStats> getStatsWithUniqHits(LocalDateTime startDate, LocalDateTime endDate, String[] uris);

    @Query("select new ru.practicum.ewm.stats.dto.ViewStats(app, uri, count(ip)) from EndpointHit " +
            "where timestamp between :startDate and :endDate " +
            "group by app, uri " +
            "order by count(distinct ip) desc")
    List<ViewStats> getStatsWithoutUris(LocalDateTime startDate, LocalDateTime endDate);

    @Query("select new ru.practicum.ewm.stats.dto.ViewStats(app, uri, count(ip)) from EndpointHit " +
            "where timestamp between :startDate and :endDate " +
            "and uri in :uris " +
            "group by app, uri " +
            "order by count(ip) desc")
    List<ViewStats> getStats(LocalDateTime startDate, LocalDateTime endDate, String[] uris);
}
