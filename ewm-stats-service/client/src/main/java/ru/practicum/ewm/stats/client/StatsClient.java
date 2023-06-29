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

import java.time.LocalDateTime;
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

    public ResponseEntity<Object> addHit(@Nullable Map<String, Object> parameters, NewEndpointHit body) {
        return makeAndSendRequest(HttpMethod.POST, "/hit", null, body);
    }

    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique) {
        if (uris == null) {
            return makeAndSendRequest(HttpMethod.POST,
                    "/stats?start={start}&end={end}&unique={unique}",
                    Map.of(
                            "start", start,
                            "end", end,
                            "unique", unique
                    ),
                    null);
        }

        return makeAndSendRequest(HttpMethod.GET,
                "/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                Map.of(
                        "start", start,
                        "end", end,
                        "uris", uris,
                        "unique", unique
                ),
                null);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, @Nullable Map<String, Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<Object> statsServerResponse;
        try {
            if (parameters != null) {
                statsServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                statsServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareServerResponse(statsServerResponse);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private static ResponseEntity<Object> prepareServerResponse(ResponseEntity<Object> response) {
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
