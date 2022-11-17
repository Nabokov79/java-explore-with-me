package ru.practicum.ewm.events.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.categories.repository.CategoriesRepository;
import ru.practicum.ewm.client.EventClient;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.QEvent;
import ru.practicum.ewm.paramRequest.ParamAdminRequest;
import ru.practicum.ewm.events.model.State;
import ru.practicum.ewm.events.repository.EventsRepository;
import ru.practicum.ewm.exeption.BadRequestException;
import ru.practicum.ewm.exeption.NotFoundException;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.events.dto.AdminUpdateEventRequest;
import ru.practicum.ewm.events.dto.EventFullDto;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminEventsServiceImpl implements AdminEventsService {

    private final EventsRepository repository;
    private final EventClient eventClient;
    private final CategoriesRepository categoriesRepository;

    @Override
    public List<EventFullDto> search(ParamAdminRequest param, HttpServletRequest request, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = repository.findAll(getRequestParam(param), pageable).getContent();
        Map<Long, Long> views = eventClient.get(events);
        log.info("Search event with parameters usersId={}, states={}, categories={}, rangeStart={}, rangeEnd={}",
                param.getUsers(), param.getStates(), param.getCategories(), param.getRangeStart(), param.getRangeEnd());
        return events.stream().map(event -> EventMapper.toEventFullDto(event, views)).collect(Collectors.toList());
    }

    @Override
    public EventFullDto edit(Long eventId, AdminUpdateEventRequest adminUpdateEvent, HttpServletRequest request) {
        Event event = getById(eventId);
        Category category = categoriesRepository.findById(adminUpdateEvent.getCategory())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Category with id= %s not found", eventId)));
        log.info("Edit event with eventId={}", eventId);
        return save(EventMapper.getEvent(event, adminUpdateEvent, category));
    }

    @Override
    public EventFullDto publish(Long eventId, HttpServletRequest request) {
        Event event = getById(eventId);
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new BadRequestException(
                    String.format("Time of the event is set incorrectly eventDate=%s", event.getEventDate()));
        }
        if (!event.getState().equals(State.PENDING)) {
            throw new BadRequestException("Event is " + event.getState());
        }
        event.setState(State.PUBLISHED);
        event.setPublishedOn(LocalDateTime.now());
        log.info("Publish event with eventId={}", eventId);
        return save(event);
    }

    @Override
    public EventFullDto reject(Long eventId, HttpServletRequest request) {
        Event event = getById(eventId);
        if (event.getState().equals(State.PUBLISHED)) {
            throw new BadRequestException("Event is " + event.getState());
        }
        event.setState(State.CANCELED);
        log.info("Reject event with eventId={}", eventId);
        return save(event);

    }

    private Event getById(Long eventId) {
        log.info("Get event with eventId={}", eventId);
        return repository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event not found by id=%s", eventId)));
    }

    private EventFullDto save(Event event) {
        repository.save(event);
        return  EventMapper.toEventFullDto(event,  eventClient.get(List.of(event)));
    }

    private BooleanBuilder getRequestParam(ParamAdminRequest param) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (param.getUsers() != null) {
            booleanBuilder.and(QEvent.event.initiator.id.in(param.getUsers()));
        }
        if (param.getStates() != null) {
            booleanBuilder.and(QEvent.event.state.in(param.getStates()));
        }
        if (param.getCategories() != null) {
            booleanBuilder.and((QEvent.event.category.id.in(param.getCategories())));
        }
        if (param.getRangeStart() != null) {
            booleanBuilder.and(QEvent.event.eventDate.after(param.getRangeStart()));
        }
        if (param.getRangeEnd() != null) {
            booleanBuilder.and(QEvent.event.eventDate.before(param.getRangeEnd()));
        }
        return booleanBuilder;
    }
}
