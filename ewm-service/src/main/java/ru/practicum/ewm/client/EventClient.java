package ru.practicum.ewm.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
public class EventClient extends StatClient {

    private final String serviceName;

    @Autowired
    public EventClient(@Value("${stat-service.url}") String serverUrl,
                       @Value("${service.name}") String serviceName,
                                                 RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
        this.serviceName = serviceName;
    }

    public void saveStat(String uri, String ip,  String timestamp) {
        EndpointHit endpointHit = new EndpointHit(serviceName,uri,ip,timestamp);
        log.info("Received request to save stat app={}, uri={}, ip={}, timestamp={}",
                endpointHit.getApp(), endpointHit.getUri(), endpointHit.getIp(), endpointHit.getTimestamp());
        post("/hit", endpointHit);
    }

    public ResponseEntity<Object> getStat(String start, String end, String uris, Boolean unique) {
	Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique
        );
        log.info("Received request to save stat start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        return get("/stats?start={start}&end={end}&uris={uris}&unique{unique}", parameters);
    }
}