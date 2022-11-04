package ru.practicum.ewm.events.service;

import ru.practicum.ewm.client.EventClient;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.client.EndpointHit;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.paramRequest.ParamUserRequest;
import ru.practicum.ewm.events.repository.EventsRepository;
import ru.practicum.ewm.exeption.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PublicEventsServiceImpl implements PublicEventsService {

    private final EventsRepository repository;
    private final EventClient eventClient;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String MAIN_SERVICE = "MainService";
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public PublicEventsServiceImpl(EventsRepository repository, EventClient eventClient) {
        this.repository = repository;
        this.eventClient = eventClient;
    }

    @Override
    public List<EventShortDto> getAllEvents(ParamUserRequest param, int from, int size, HttpServletRequest request) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> eventList = repository.findAllEventsByCategoriesIdListAndPaid(param.getCategories(),
                List.of(param.getPaid()),
                pageable).stream().filter(event -> event.getAnnotation().toLowerCase().contains(param.getText())
                || event.getDescription().toLowerCase().contains(param.getText())).collect(Collectors.toList());
        if (param.getRangeStart() != null) {
            eventList = eventList.stream().filter(event -> event.getEventDate().isAfter(param.getRangeStart())
                                                        && event.getEventDate().isBefore(param.getRangeEnd()))
                                         .collect(Collectors.toList());
            logger.info("Get event by request parameters and with rangeStart={}, rangeEnd={}", param.getRangeStart(),
                                                                                                param.getRangeEnd());
        } else {
            eventList = eventList.stream()
                                 .filter(event -> event.getEventDate().isAfter(LocalDateTime.now()))
                                 .collect(Collectors.toList());
            logger.info("Get event by request parameters and with rangeStart={}, rangeEnd={}, dataTime={}",
                                                                                                param.getRangeStart(),
                                                                                                param.getRangeEnd(),
                                                                                                LocalDateTime.now());
        }
        if (param.getOnlyAvailable()) {
            eventList = eventList.stream().filter(event -> event.getParticipantLimit() >= 0)
                                          .collect(Collectors.toList());
        }
        Map<Long, Event> idsMap = eventList.stream().collect(Collectors.toMap(Event::getId, Function.identity()));
        String ids = idsMap.entrySet().stream().map(entry -> entry.getKey() + " = " + entry.getValue())
                .collect(Collectors.joining(";", "[","]"));
        saveStat(ids, request);
        List<EventShortDto> eventShortDtoList = eventList.stream().map(EventMapper::toEventShortDto)
                                                                  .collect(Collectors.toList());
        switch (param.getSort()) {
            case VIEWS:
                eventShortDtoList = eventShortDtoList.stream()
                                                     .sorted(Comparator.comparing(EventShortDto::getViews))
                                                     .collect(Collectors.toList());
                break;
            case EVENT_DATE:
                eventShortDtoList = eventShortDtoList.stream()
                                                     .sorted(Comparator.comparing(EventShortDto::getEventDate))
                                                     .collect(Collectors.toList());
                break;
            default:
        }
        return eventShortDtoList;
    }

    @Override
    public EventFullDto getFullInfoEvent(Long id, HttpServletRequest request) {
        Event event = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Event not found by id=%s", id)));
        saveStat(String.valueOf(event.getId()), request);
        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        eventFullDto.setViews(getStatByUri(List.of(request.getRequestURI())));
        logger.info("Get event by id={}", id);
        return eventFullDto;
    }

    private void saveStat(String ids, HttpServletRequest request) {
        eventClient.saveStat(new EndpointHit(ids, MAIN_SERVICE, request.getRequestURI(),
                request.getRemoteAddr(), LocalDateTime.now().format(DATE_TIME_FORMATTER)));
    }

    private long getStatByUri(List<String> uri) {
        String uris = String.join(",",uri);
        Object stat = eventClient.getStat("","", uris, false).getBody();
        long list = 0L;
        if (stat != null) {
            list = Arrays.asList(stat, EndpointHit.class).size();
        }
        return list;
    }
}