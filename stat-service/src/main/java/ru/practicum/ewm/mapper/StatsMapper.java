package ru.practicum.ewm.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.ewm.model.App;
import ru.practicum.ewm.model.Stat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StatsMapper {

    public static final DateTimeFormatter DATA_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Stat toStat(EndpointHit endpointHit, String uris, App app) {
        Stat stat = new Stat();
        stat.setApp(app);
        stat.setUri(uris);
        stat.setIp(endpointHit.getIp());
        stat.setTimestamp(LocalDateTime.parse(endpointHit.getTimestamp(), DATA_TIME_FORMATTER));
        return stat;
    }
}