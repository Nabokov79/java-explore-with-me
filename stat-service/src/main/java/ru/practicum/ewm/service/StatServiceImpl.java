package ru.practicum.ewm.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStats;
import ru.practicum.ewm.exeption.BadRequestException;
import ru.practicum.ewm.mapper.StatsMapper;
import ru.practicum.ewm.model.App;
import ru.practicum.ewm.model.QStat;
import ru.practicum.ewm.model.Stat;
import ru.practicum.ewm.repository.AppRepository;
import ru.practicum.ewm.repository.StatRepository;
import org.springframework.stereotype.Service;
import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.*;

import static ru.practicum.ewm.model.QApp.app;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatServiceImpl implements StatService {
    private final StatRepository repository;
    private final AppRepository appRepository;
    private final EntityManager entityManager;
    private static final String COUNT_ALIAS = "hits";

    @Override
    public void saveStat(EndpointHitDto endpointHitDto) {
        BooleanExpression booleanExpression = app.name.eq(endpointHitDto.getApp());
        App app = appRepository.findOne(booleanExpression).orElse(new App(null, endpointHitDto.getApp()));
        appRepository.save(app);
        repository.saveAll(StatsMapper.toListStat(endpointHitDto.getUri(), endpointHitDto, app));
        log.info("Save stat with param app={}, uri={}", endpointHitDto.getApp(), endpointHitDto.getUri());
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
            QStat stat = QStat.stat;
            JPAQueryFactory qf = new JPAQueryFactory(entityManager);
            JPAQuery<Stat> query = qf.from(stat)
                    .select(
                            Projections.constructor(
                                    Stat.class,
                                    app.name,
                                    stat.uri,
                                    unique ? stat.ip.countDistinct().as(COUNT_ALIAS) : stat.ip.count().as(COUNT_ALIAS)
                            )
                    )
                    .join(app).on(stat.app.id.eq(app.id))
                    .where(booleanBuilder)
                    .groupBy(stat.uri, app.name);
            statListDb = query.fetch();
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

