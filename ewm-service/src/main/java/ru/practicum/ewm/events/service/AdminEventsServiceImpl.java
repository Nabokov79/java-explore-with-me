package ru.practicum.ewm.events.service;

import ru.practicum.ewm.categories.repository.CategoriesRepository;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.paramRequest.ParamAdminRequest;
import ru.practicum.ewm.events.model.State;
import ru.practicum.ewm.paramRequest.Param;
import ru.practicum.ewm.events.repository.EventsRepository;
import ru.practicum.ewm.exeption.BadRequestException;
import ru.practicum.ewm.exeption.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.events.dto.AdminUpdateEventRequest;
import ru.practicum.ewm.events.dto.EventFullDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminEventsServiceImpl implements AdminEventsService {

    private final EventsRepository repository;
    private final CategoriesRepository categoriesRepository;

    @Autowired
    public AdminEventsServiceImpl(EventsRepository repository, CategoriesRepository categoriesRepository) {
        this.repository = repository;
        this.categoriesRepository = categoriesRepository;
    }

    @Override
    public List<EventFullDto> searchEvents(ParamAdminRequest param, int from, int size) {
        List<Event> eventList = repository.findAllEventsByInitiatorIdListAndCategoriesIdList(
                                                                      param.getUsers(), param.getCategories()).stream()
                                   .filter(event -> event.getEventDate().isAfter(param.getRangeStart())
                                   && event.getEventDate().isBefore(param.getRangeEnd())).collect(Collectors.toList());
        for (String state : param.getStates()) {
            eventList = eventList.stream().filter(event -> event.getState().toString().equals(state))
                    .collect(Collectors.toList());
        }
        return eventList.stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto editEvent(Long eventId, AdminUpdateEventRequest adminUpdateEvent) {
        Event event = getEventById(eventId);
        event.setAnnotation(adminUpdateEvent.getAnnotation());
        event.setCategory(categoriesRepository.findById(adminUpdateEvent.getCategory())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Category with id= %s not found", eventId))));
        event.setDescription(adminUpdateEvent.getDescription());
        event.setEventDate(LocalDateTime.parse(adminUpdateEvent.getEventDate(), Param.DATE_TIME_FORMATTER));
        event.setPaid(adminUpdateEvent.getPaid());
        event.setParticipantLimit(adminUpdateEvent.getParticipantLimit());
        event.setRequestModeration(adminUpdateEvent.getRequestModeration());
        event.setTitle(adminUpdateEvent.getTitle());
        repository.save(event);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto publishEvent(Long eventId) {
        Event event = getEventById(eventId);
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new BadRequestException(
                             String.format("Time of the event is set incorrectly eventDate=%s", event.getEventDate()));
        }
        if (event.getState().equals(State.PUBLISHED) || event.getState().equals(State.CANCELED)) {
            throw new BadRequestException("Event is " + event.getState());
        }
        event.setState(State.PUBLISHED);
        event.setPublishedOn(LocalDateTime.now());
        repository.save(event);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto rejectEvent(Long eventId) {
        Event event = getEventById(eventId);
        if (event.getState().equals(State.PUBLISHED)) {
            throw new BadRequestException("Event is " + event.getState());
        }
        event.setState(State.CANCELED);
        repository.save(event);
        return EventMapper.toEventFullDto(event);
    }

    private Event getEventById(Long eventId) {
        return repository.findById(eventId)
                .orElseThrow(() ->  new NotFoundException(String.format("Event not found by id=%s", eventId)));
    }
}
