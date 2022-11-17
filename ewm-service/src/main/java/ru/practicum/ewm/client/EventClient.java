package ru.practicum.ewm.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.events.dto.ViewStats;
import ru.practicum.ewm.events.model.Event;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
public class EventClient {

   private final StatClient client;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String serviceName;

    @Autowired
    public EventClient(@Value("${service.name}") String serviceName, StatClient client) {
        this.serviceName = serviceName;
        this.client = client;
    }

    public Map<Long, Long> get(List<Event> events) {
        List<ViewStats> stats = client.get("/stats","", "", getURIList(events), false);
        Map<Long, Long> hits = new HashMap<>();
        stats.forEach(stat -> hits.put(Long.parseLong(stat.getUri().split("/")[2]), stat.getHits()));
        log.info("Received request to get stat for events={}", events);
        return hits;
    }

    public void save(String uri, String ip) {
        log.info("Received request to save state stat with urls={}, ip={}",uri, ip);
        client.post("/hit", new EndpointHit(serviceName,
                                                 uri,
                                                 ip,
                                                 LocalDateTime.now().format(DATE_TIME_FORMATTER)));
    }

    private List<String> getURIList(List<Event> events) {
        return new ArrayList<>(events.stream()
                .collect(Collectors.toMap(Event::getId, event -> event)).keySet())
                .stream()
                .map(e -> "/events/" + e)
                .collect(Collectors.toList());
    }
}