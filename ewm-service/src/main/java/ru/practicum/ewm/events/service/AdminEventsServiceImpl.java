package ru.practicum.ewm.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.categories.repository.CategoriesRepository;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.paramRequest.ParamAdminRequest;
import ru.practicum.ewm.events.model.State;
import ru.practicum.ewm.events.repository.EventsRepository;
import ru.practicum.ewm.exeption.BadRequestException;
import ru.practicum.ewm.exeption.NotFoundException;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.events.dto.AdminUpdateEventRequest;
import ru.practicum.ewm.events.dto.EventFullDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminEventsServiceImpl implements AdminEventsService {

    private final EventsRepository repository;
    private final CategoriesRepository categoriesRepository;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<EventFullDto> search(ParamAdminRequest param, int from, int size) {
        List<Event> events = repository.findAllEventsByInitiatorIdListAndCategoriesIdList(
                                                                      param.getUsers(), param.getCategories()).stream()
                                   .filter(event -> event.getEventDate().isAfter(param.getRangeStart())
                                   && event.getEventDate().isBefore(param.getRangeEnd())).collect(Collectors.toList());
        for (String state : param.getStates()) {
            events = events.stream().filter(event -> event.getState().toString().equals(state))
                    .collect(Collectors.toList());
        }
        log.info("Search event with parameters usersId={}, states={}, categories={}, rangeStart={}, rangeEnd={}",
                param.getUsers(), param.getStates(), param.getCategories(), param.getRangeStart(), param.getRangeEnd());
        return events.stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto edit(Long eventId, AdminUpdateEventRequest adminUpdateEvent) {
        Event event = getById(eventId);
        event.setAnnotation(adminUpdateEvent.getAnnotation());
        event.setCategory(categoriesRepository.findById(adminUpdateEvent.getCategory())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Category with id= %s not found", eventId))));
        event.setDescription(adminUpdateEvent.getDescription());
        event.setEventDate(LocalDateTime.parse(adminUpdateEvent.getEventDate(), DATE_TIME_FORMATTER));
        event.setPaid(adminUpdateEvent.isPaid());
        event.setParticipantLimit(adminUpdateEvent.getParticipantLimit());
        event.setRequestModeration(adminUpdateEvent.isRequestModeration());
        event.setTitle(adminUpdateEvent.getTitle());
        repository.save(event);
        log.info("Edit event with eventId={}", eventId);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto publish(Long eventId) {
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
        repository.save(event);
        log.info("Publish event with eventId={}", eventId);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto reject(Long eventId) {
        Event event = getById(eventId);
        if (event.getState().equals(State.PUBLISHED)) {
            throw new BadRequestException("Event is " + event.getState());
        }
        event.setState(State.CANCELED);
        repository.save(event);
        log.info("Reject event with eventId={}", eventId);
        return EventMapper.toEventFullDto(event);
    }

    private Event getById(Long eventId) {
        log.info("Get event with eventId={}", eventId);
        return repository.findById(eventId)
                .orElseThrow(() ->  new NotFoundException(String.format("Event not found by id=%s", eventId)));
    }
}
