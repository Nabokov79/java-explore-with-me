package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.ewm.dto.ViewStats;
import ru.practicum.ewm.mapper.StatsMapper;
import ru.practicum.ewm.model.Stat;
import ru.practicum.ewm.repository.StatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatServiceImpl implements StatService {
    private final StatRepository repository;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public StatServiceImpl(StatRepository repository) {
        this.repository = repository;
    }

    @Override
    public void saveStat(EndpointHit endpointHit) {
        Stat stat = StatsMapper.toStat(endpointHit);
        repository.save(stat);
        logger.info("Save stat with param app={}", endpointHit.getApp());
    }

    @Override
    public List<ViewStats> getAllStats(String start, String end, String uris, Boolean unique) {
        List<String> urisList = Arrays.stream(uris.split(",")).collect(Collectors.toList());
        List<Stat> statListDb;
        if (start != null && end != null) {
            LocalDateTime start1 = LocalDateTime.parse(start, StatsMapper.DATA_TIME_FORMATTER);
            LocalDateTime end1 = LocalDateTime.parse(end, StatsMapper.DATA_TIME_FORMATTER);
            statListDb = repository.findAllByUri(urisList).stream()
                                                       .filter(endpointHit -> endpointHit.getTimestamp().isAfter(start1)
                                                                           && endpointHit.getTimestamp().isBefore(end1))
                                                        .collect(Collectors.toList());
        } else {
            statListDb = repository.findAllByUri(urisList);
        }

        if (unique) {
            statListDb = new ArrayList<>(new TreeSet<>(statListDb));
        }
        List<ViewStats> statList = new ArrayList<>();
        for (Stat val : statListDb) {
            long hits = statListDb.stream().filter(endpointHit -> endpointHit.getUri().equals(val.getUri())).count();
            statList.add(new ViewStats(val.getApp(), val.getUri(), hits));
        }
        logger.info("Get all stat by param start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        return statList;
    }
}