package ru.practicum.ewm.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.model.App;
import ru.practicum.ewm.model.Stat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StatsMapper {

    public static final DateTimeFormatter DATA_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Stat toStat(EndpointHitDto endpointHit, String uris, App app) {
        Stat stat = new Stat();
        stat.setApp(app);
        stat.setUri(uris);
        stat.setIp(endpointHit.getIp());
        stat.setTimestamp(LocalDateTime.parse(endpointHit.getTimestamp(), DATA_TIME_FORMATTER));
        return stat;
    }

    public static List<Stat> toListStat(List<String> uris, EndpointHitDto endpointHitDto, App app) {
        List<Stat> stats = new ArrayList<>();
        for (String uri : uris) {
            stats.add(toStat(endpointHitDto, uri,app));
        }
        return stats;
    }
}