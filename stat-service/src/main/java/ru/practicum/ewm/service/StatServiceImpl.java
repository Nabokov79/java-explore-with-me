package ru.practicum.ewm.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.ewm.dto.ViewStats;
import ru.practicum.ewm.exeption.BadRequestException;
import ru.practicum.ewm.mapper.StatsMapper;
import ru.practicum.ewm.model.App;
import ru.practicum.ewm.model.QApp;
import ru.practicum.ewm.model.QStat;
import ru.practicum.ewm.model.Stat;
import ru.practicum.ewm.repository.AppRepository;
import ru.practicum.ewm.repository.StatRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.swing.text.html.parser.Entity;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatServiceImpl implements StatService {
    private final StatRepository repository;
    private final AppRepository appRepository;
    private final EntityManager em;

    @Override
    public void saveStat(EndpointHit endpointHit) {
        BooleanExpression booleanExpression = QApp.app.name.eq(endpointHit.getApp());
        App app = appRepository.findOne(booleanExpression).orElse(new App(null, endpointHit.getApp()));
        appRepository.save(app);
        repository.save(StatsMapper.toStat(endpointHit, app));
        log.info("Save stat with param app={}", endpointHit.getApp());
    }

    @Override
    public List<ViewStats> getAllStats(String start, String end, List<String> uris, Boolean unique) {
        List<Stat> statListDb;
        if (!start.isEmpty() && !end.isEmpty()) {
            BooleanBuilder booleanBuilder = new BooleanBuilder();
            if (!uris.isEmpty()) {
                booleanBuilder.and(QStat.stat.uri.in(uris));
            } else {
                throw new BadRequestException(String.format("Uri list empty uris=%s", uris));
            }
            LocalDateTime start1 = LocalDateTime.parse(start, StatsMapper.DATA_TIME_FORMATTER);
            LocalDateTime end1 = LocalDateTime.parse(end, StatsMapper.DATA_TIME_FORMATTER);
            booleanBuilder.and(QStat.stat.timestamp.after(start1));
            booleanBuilder.and(QStat.stat.timestamp.before(end1));
            statListDb = (List<Stat>) repository.findAll(booleanBuilder);
        } else {
            statListDb = repository.findAllByUri(uris);
        }

        if (unique) {
            statListDb = new ArrayList<>(new TreeSet<>(statListDb));
        }
        List<ViewStats> statList = new ArrayList<>();
        for (Stat val : statListDb) {
            long hits = statListDb.stream().filter(endpointHit -> endpointHit.getUri().equals(val.getUri())).count();
            statList.add(new ViewStats(val.getApp().getName(), val.getUri(), hits));
        }
        log.info("Get all stat by param start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        return statList;
    }
}