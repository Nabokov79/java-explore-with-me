package ru.practicum.ewm.events.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.client.EventClient;
import ru.practicum.ewm.events.mapper.EventMapper;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicEventsServiceImpl implements PublicEventsService {

    private final EventsRepository repository;
    private final EventClient eventClient;

    @Override
    public List<EventShortDto> getAll(ParamUserRequest param, int from, int size, HttpServletRequest request) {
        log.info("Sort = " + (param.getSort()));
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = repository.findAll(getRequestParamForDb(param), pageable).getContent();
        Map<Long, Long> views = eventClient.get(events);
        List<EventShortDto> eventShort = events.stream()
                .map(event -> EventMapper.toEventShortDto(event, views))
                .collect(Collectors.toList());
        if (param.getSort() != null) {
            switch (param.getSort()) {
                case VIEWS:
                    eventShort = eventShort.stream()
                            .sorted(Comparator.comparing(EventShortDto::getViews))
                            .collect(Collectors.toList());
                    break;
                case EVENT_DATE:
                    eventShort = eventShort.stream()
                            .sorted(Comparator.comparing(EventShortDto::getEventDate))
                            .collect(Collectors.toList());
                    break;
            }
        }
        log.info("Get all events with param: text={}, categories={}, paid={}, rangeStart={}," +
                        " rangeEnd={}, onlyAvailable={}, sort={}",
                param.getText(), param.getCategories(), param.getPaid(), param.getRangeStart(), param.getRangeEnd(),
                param.getOnlyAvailable(), param.getSort());
        return eventShort;
    }

    @Override
    public EventFullDto getFullInfo(Long id, HttpServletRequest request) {
        Event event = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Event not found by id=%s", id)));
        eventClient.save(request.getRequestURI(), request.getRemoteAddr());
        log.info("Get full info for event eventId={}", id);
        return EventMapper.toEventFullDto(event, eventClient.get(List.of(event)));
    }

    private BooleanBuilder getRequestParamForDb(ParamUserRequest param) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(QEvent.event.state.eq(State.PUBLISHED));
        if (param.getText().isBlank()) {
            String text = param.getText();
            booleanBuilder.and((QEvent.event.annotation.containsIgnoreCase(text)
                    .or(QEvent.event.description.containsIgnoreCase(text))));
        }
        if (param.getCategories() != null) {
            booleanBuilder.and(QEvent.event.category.id.in(param.getCategories()));
        }
        if (param.getPaid() != null) {
            booleanBuilder.and(QEvent.event.paid.eq(param.getPaid()));
        }
        if (param.getRangeStart() != null) {
            booleanBuilder.and(QEvent.event.eventDate.after(param.getRangeStart()));
        }
        if (param.getRangeEnd() != null) {
            booleanBuilder.and(QEvent.event.eventDate.before(param.getRangeEnd()));
        }
        if (param.getOnlyAvailable() != null) {
            booleanBuilder.and(QEvent.event.requests.size().lt(QEvent.event.participantLimit));
        }
        return booleanBuilder;
    }
}