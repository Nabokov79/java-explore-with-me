package ru.practicum.ewm.client;

import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.ewm.events.dto.ViewStats;


@Component
@RequiredArgsConstructor
public class StatClient {
    private final WebClient webClient;

    public void post(String path, EndpointHit endpointHit) {
        webClient.post()
                .uri(path)
                .bodyValue(endpointHit)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public List<ViewStats> get(String path, String start, String end, List<String> uris, Boolean unique) {
        return Objects.requireNonNull(webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path(path)
                                .queryParam("start", start)
                                .queryParam("end", end)
                                .queryParam("uris", uris)
                                .queryParam("unique", unique)
                                .build())
                        .retrieve()
                        .toEntityList(ViewStats.class)
                        .block())
                .getBody();

    }
}