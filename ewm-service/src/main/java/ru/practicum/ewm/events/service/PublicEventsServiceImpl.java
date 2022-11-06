package ru.practicum.ewm.events.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.client.EventClient;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.client.EndpointHit;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.QEvent;
import ru.practicum.ewm.events.model.State;
import ru.practicum.ewm.paramRequest.ParamUserRequest;
import ru.practicum.ewm.events.repository.EventsRepository;
import ru.practicum.ewm.exeption.NotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicEventsServiceImpl implements PublicEventsService {

    private final EventsRepository repository;
    private final EventClient eventClient;
    private static final String MAIN_SERVICE = "MainService";

    @Override
    public List<EventShortDto> getAll(ParamUserRequest param, int from, int size, HttpServletRequest request) {
        Pageable pageable = PageRequest.of(from / size, size);
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(QEvent.event.state.eq(State.PUBLISHED));
        if (param.getCategories() != null) {
            booleanBuilder.and(QEvent.event.category.id.in(param.getCategories()));
        }
        if (param.getPaid() != null) {
            booleanBuilder.and(QEvent.event.paid.eq(param.getPaid()));
        }
        if (param.getText() != null) {
            String text = param.getText();
            booleanBuilder.and((QEvent.event.annotation.containsIgnoreCase(text)
                    .or(QEvent.event.description.containsIgnoreCase(text))));
        }
        if (param.getRangeStart() != null) {
            booleanBuilder.and(QEvent.event.eventDate.after(param.getRangeStart()));
        }
        if (param.getRangeEnd() != null) {
            booleanBuilder.and(QEvent.event.eventDate.before(param.getRangeEnd()));
        }
        if (param.getOnlyAvailable() != null) {
            booleanBuilder.and(QEvent.event.confirmedRequests.size().lt(QEvent.event.participantLimit));
        }
        List<EventShortDto> eventsShortDto =
                                       EventMapper.toListDto(repository.findAll(booleanBuilder, pageable).getContent());
        switch (param.getSort()) {
            case VIEWS:
                eventsShortDto = eventsShortDto.stream().sorted(Comparator.comparing(EventShortDto::getViews))
                                                        .collect(Collectors.toList());
                break;
            case EVENT_DATE:
                eventsShortDto = eventsShortDto.stream().sorted(Comparator.comparing(EventShortDto::getEventDate))
                                                       .collect(Collectors.toList());
                break;
            default:
        }
        saveStat(request);
        return eventsShortDto;
    }

    @Override
    public EventFullDto getFullInfo(Long id, HttpServletRequest request) {
        Event event = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Event not found by id=%s", id)));
        saveStat(request);
        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        eventFullDto.setViews(getStatByUri(request.getRequestURI()));
        log.info("Get event by id={}", id);
        return eventFullDto;
    }

    private void saveStat(HttpServletRequest request) {
        eventClient.saveStat(new EndpointHit(MAIN_SERVICE, request.getRequestURI(),
                                                           request.getRemoteAddr(),
                                                           LocalDateTime.now().format(
                                                                   DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                                                                                    ));
    }

    private long getStatByUri(String uri) {
        String uris = String.join(",",uri);
        Object stat = eventClient.getStat("","", uris, false).getBody();
        long list = 0L;
        if (stat != null) {
            list = Arrays.asList(stat, EndpointHit.class).size();
        }
        return list;
    }
}