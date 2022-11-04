package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStats;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.repository.StatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatServiceImpl implements StatService {
    private final StatRepository repository;
    public static final DateTimeFormatter DATA_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public StatServiceImpl(StatRepository repository) {
        this.repository = repository;
    }

    @Override
    public void saveStat(EndpointHitDto endpointHit) {
        List<EndpointHit> endpointHitList = new ArrayList<>();
        logger.info("Save stat with param id={}", endpointHit.getId());
        List<Long> ids = Arrays.stream(endpointHit.getId().split(","))
                                                           .map(Long::valueOf)
                                                           .collect(Collectors.toList());
        for (Long id : ids) {
            endpointHitList.add(new EndpointHit(id, endpointHit.getApp(), endpointHit.getUri(),
                                                    endpointHit.getIp(), endpointHit.getTimestamp()));
        }
        repository.saveAll(endpointHitList);
    }

    @Override
    public List<ViewStats> getAllStats(String start, String end, String uris, Boolean unique) {
        List<String> urisList = Arrays.stream(uris.split(",")).collect(Collectors.toList());
        List<EndpointHit> statListDb;
        if (start != null && end != null) {
            LocalDateTime start1 = LocalDateTime.parse(start, DATA_TIME_FORMATTER);
            LocalDateTime end1 = LocalDateTime.parse(end, DATA_TIME_FORMATTER);
            statListDb = repository.findAllByUri(urisList).stream()
                    .filter(endpointHit -> LocalDateTime.parse(endpointHit.getTimestamp(), DATA_TIME_FORMATTER).isAfter(start1)
                            && LocalDateTime.parse(endpointHit.getTimestamp(), DATA_TIME_FORMATTER).isBefore(end1))
                    .collect(Collectors.toList());
        } else {
            statListDb = repository.findAllByUri(urisList);
        }

        if (unique) {
            statListDb = new ArrayList<>(new TreeSet<>(statListDb));
        }
        List<ViewStats> statList = new ArrayList<>();
        for (EndpointHit val : statListDb) {
            long hits = statListDb.stream().filter(endpointHit -> endpointHit.getUri().equals(val.getUri())).count();
            statList.add(new ViewStats(val.getApp(), val.getUri(), hits));
        }
        logger.info("Get all stat by param start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        return statList;
    }
}