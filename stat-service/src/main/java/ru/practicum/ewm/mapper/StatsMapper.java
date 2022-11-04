package ru.practicum.ewm.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.ewm.model.Stat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class StatsMapper {

    public static final DateTimeFormatter DATA_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Stat toStat(EndpointHit endpointHitDto) {
        Stat stat = new Stat();
        stat.setApp(endpointHitDto.getApp());
        stat.setUri(endpointHitDto.getUri());
        stat.setIp(endpointHitDto.getIp());
        stat.setTimestamp(LocalDateTime.parse(endpointHitDto.getTimestamp(), DATA_TIME_FORMATTER));
        return stat;
    }
}
