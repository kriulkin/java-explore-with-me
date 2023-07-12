package ru.practicum.ewm.stats.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stats.dto.NewEndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;
import ru.practicum.ewm.stats.server.model.EndpointHit;
import ru.practicum.ewm.stats.server.service.HitService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class HitController {
    private final HitService hitService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHit addHit(@Valid @RequestBody NewEndpointHit hit) {
        log.info("Post hit info {}", hit);
        return hitService.addHit(hit);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam @NotBlank String start,
                                    @RequestParam @NotBlank String end,
                                    @RequestParam(required = false) String[] uris,
                                    @RequestParam(defaultValue = "false") boolean unique) {
        log.info("Get stats from {} to {} period", start, end);
        return hitService.getStats(start, end, uris, unique);
    }
}
