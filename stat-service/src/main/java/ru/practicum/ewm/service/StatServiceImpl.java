package ru.practicum.ewm.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.ewm.dto.ViewStats;
import ru.practicum.ewm.exeption.BadRequestException;
import ru.practicum.ewm.mapper.StatsMapper;
import ru.practicum.ewm.model.App;
import ru.practicum.ewm.model.QStat;
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
    public void saveStat(EndpointHit endpointHit) {
        BooleanExpression booleanExpression = app.name.eq(endpointHit.getApp());
        App app = appRepository.findOne(booleanExpression).orElse(new App(null, endpointHit.getApp()));
        appRepository.save(app);
        repository.save(StatsMapper.toStat(endpointHit, endpointHit.getUri(), app));
        log.info("Save stat with param app={}, uri={}", endpointHit.getApp(), endpointHit.getUri());
    }

    @Override
    public List<ViewStats> getAllStats(String start, String end, List<String> uris, Boolean unique) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!uris.isEmpty()) {
            booleanBuilder.and(QStat.stat.uri.in(uris));
        } else {
            throw new BadRequestException(String.format("Uri list empty uris=%s", uris));
        }
        if (!start.isEmpty() && !end.isEmpty()) {
            booleanBuilder.and(QStat.stat.timestamp.after(LocalDateTime.parse(start, StatsMapper.DATA_TIME_FORMATTER)));
            booleanBuilder.and(QStat.stat.timestamp.before(LocalDateTime.parse(end, StatsMapper.DATA_TIME_FORMATTER)));
        }
        QStat stat = QStat.stat;
        JPAQueryFactory qf = new JPAQueryFactory(entityManager);
        JPAQuery<ViewStats> query = qf.from(stat)
                .select(
                        Projections.constructor(
                                ViewStats.class,
                                app.name,
                                stat.uri,
                                unique ? stat.ip.countDistinct().as(COUNT_ALIAS) : stat.ip.count().as(COUNT_ALIAS)
                        )
                )
                .join(app).on(stat.app.id.eq(app.id))
                .where(booleanBuilder)
                .groupBy(stat.uri, app.name);
        log.info("Get all stat by param start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        return query.fetch();
    }
}

