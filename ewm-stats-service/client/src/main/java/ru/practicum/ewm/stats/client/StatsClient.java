package ru.practicum.ewm.stats.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.stats.dto.NewEndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatsClient {
    private final RestTemplate rest;

    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        rest = builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public void addHit(NewEndpointHit body) {
        HttpEntity<NewEndpointHit> requestEntity = new HttpEntity<>(body, defaultHeaders());
        ResponseEntity<ViewStats> statsServerResponse;

        try {
            statsServerResponse = rest.exchange("/hit", HttpMethod.POST, requestEntity, ViewStats.class);
        } catch (HttpStatusCodeException e) {
            throw new StatsClientRequestException(String.format("The request for statistic is failed due to %s", e.getMessage()));
        }

        prepareServerResponse(statsServerResponse);
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", URLEncoder.encode(start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), StandardCharsets.UTF_8));
        parameters.put("end", URLEncoder.encode(end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), StandardCharsets.UTF_8));
        parameters.put("unique", unique);

        if (uris == null) {
            return List.of(makeAndSendRequest(HttpMethod.GET,
                    "/stats?start={start}&end={end}&unique={unique}",
                    parameters,
                    null).getBody());
        }

        parameters.put("uris", uris);
        return List.of(makeAndSendRequest(HttpMethod.GET,
                "/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                parameters,
                null).getBody());
    }

    private <T> ResponseEntity<ViewStats[]> makeAndSendRequest(HttpMethod method, String path, @Nullable Map<String, Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<ViewStats[]> statsServerResponse;
        try {
            if (parameters != null) {
                statsServerResponse = rest.exchange(path, method, requestEntity, ViewStats[].class, parameters);
            } else {
                statsServerResponse = rest.exchange(path, method, requestEntity, ViewStats[].class);
            }
        } catch (HttpStatusCodeException e) {
            throw new StatsClientRequestException(String.format("The request for statistic is failed due to %s", e.getMessage()));
        }
        return prepareServerResponse(statsServerResponse);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private static <T> ResponseEntity<T> prepareServerResponse(ResponseEntity<T> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}
