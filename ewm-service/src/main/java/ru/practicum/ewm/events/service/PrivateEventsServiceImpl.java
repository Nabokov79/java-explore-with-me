package ru.practicum.ewm.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.categories.repository.CategoriesRepository;
import ru.practicum.ewm.client.EventClient;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.dto.UpdateEventRequest;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.State;
import ru.practicum.ewm.exeption.BadRequestException;
import ru.practicum.ewm.exeption.NotFoundException;
import ru.practicum.ewm.requests.dto.ParticipationRequestDto;
import ru.practicum.ewm.events.repository.EventsRepository;
import ru.practicum.ewm.requests.mapper.RequestsMapper;
import ru.practicum.ewm.requests.model.Request;
import ru.practicum.ewm.requests.model.Status;
import ru.practicum.ewm.requests.repository.RequestsRepository;
import ru.practicum.ewm.users.repository.UsersRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrivateEventsServiceImpl implements PrivateEventsService {

    private final EventsRepository repository;
    private final EventClient eventClient;
    private final UsersRepository usersRepository;
    private final CategoriesRepository categoriesRepository;
    private final RequestsRepository requestsRepository;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<EventFullDto> getByUserId(Long userId, int from, int size, HttpServletRequest request) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = repository.findAllByInitiatorId(userId, pageable);
        Map<Long, Long> views = eventClient.get(events);
        log.info("Get event by userId={} with parameters from={}, size={}", userId, from, size);
        return events.stream().map(event -> EventMapper.toEventFullDto(event, views)).collect(Collectors.toList());
    }

    @Override
    public EventFullDto changeByUserId(Long userId, UpdateEventRequest eventRequest, HttpServletRequest request) {
        Event event = get(eventRequest.getEventId(), userId);
        if (LocalDateTime.parse(eventRequest.getEventDate(), DATE_TIME_FORMATTER)
                                                                          .isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException(
                           String.format("Time of the event is set incorrectly eventDate=%s", event.getEventDate()));
        }
        if (event.getState().equals(State.PUBLISHED)) {
            throw new BadRequestException("Event is published, cannot be changed");
        }
        if (event.getState().equals(State.CANCELED)) {
            event.setPaid(true);
        }
        log.info("Update event by initiator userId={} ", userId);
        return save(getModifiedEvent(event, eventRequest));
    }

    @Override
    public EventFullDto create(Long userId, NewEventDto newEventDto, HttpServletRequest request) {
        Event event = EventMapper.toEvent(newEventDto);
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException(
                    String.format("Time of the event is set incorrectly eventDate=%s", event.getEventDate()));
        }
        event.setInitiator(usersRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                                                                  String.format("User with id=%s not found", userId))));
        event.setCategory(categoriesRepository.findById(newEventDto.getCategory())
                                          .orElseThrow(() -> new NotFoundException(
                                          String.format("Category with id= %s not found", newEventDto.getCategory()))));
        log.info("Create event={}", event);
        return save(event);
    }

    @Override
    public EventFullDto getInfoCurrentUser(Long userId, Long eventId, HttpServletRequest request) {
        Event event = get(eventId, userId);
        log.info("Get event info current user userId={}", userId);
        return EventMapper.toEventFullDto(event, eventClient.get(List.of(event)));
    }

    @Override
    public EventFullDto cancel(Long userId, Long eventId, HttpServletRequest request) {
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event not found with eventId=%s", eventId)));
        if (event.getInitiator().getId() != userId) {
            throw new BadRequestException(String.format("User is not the initiator of the event? user=%s", userId));
        }
        if (!event.getState().equals(State.PENDING)) {
            throw new BadRequestException(String.format("Event moderation is required, state=%s", event.getState()));
        }
        event.setState(State.CANCELED);
        log.info("Event cancel eventId={}", eventId);
        return save(event);
    }

    @Override
    public List<ParticipationRequestDto> getInfoRequestEventsCurrentUser(Long userId, Long eventId) {
        List<Request> requests = new ArrayList<>(requestsRepository.findByInitiatorIdAndRequesterId(userId, eventId));
        if (requests.isEmpty()) {
            throw new NotFoundException(String.format("Request not found with userId=%s, eventId=%s", userId, eventId));
        }
        log.info("Get info request events current user userId={}, eventId={}",userId, eventId);
        return RequestsMapper.toListDto(requests);
    }

    @Override
    public ParticipationRequestDto confirmRequestUsers(Long userId, Long eventId, Long reqId) {
        Request requestDb = requestsRepository.findById(reqId)
                .orElseThrow(() -> new NotFoundException(String.format("Request with id= %s was not found", userId)));
        Event event = get(eventId, userId);
        Set<Request> requestList = requestsRepository.findAllByEventId(eventId);
        long countDb = requestsRepository.countAllByEventIdAndStatus(eventId, Status.CONFIRMED);
        long count = Long.valueOf(event.getParticipantLimit());
        if (countDb < count) {
            requestDb.setStatus(Status.CONFIRMED);
            requestsRepository.save(requestDb);
        }
        if (countDb == count) {
            requestList.forEach(request -> request.setCreated(event.getEventDate()));
            requestList.forEach(request -> request.setStatus(Status.CANCELED));
            requestsRepository.saveAll(requestList);
        }
        requestsRepository.save(requestDb);
        log.info("Confirm request users userId={}, eventId={}, reqId={}",userId, eventId, reqId);
        return RequestsMapper.toParticipationRequestDto(requestDb);
    }

    @Override
    public ParticipationRequestDto rejectRequestUsers(Long userId, Long eventId, Long reqId) {
        List<Request> requests = requestsRepository.findByIdAndEventId(reqId, eventId).stream()
                                                    .filter(request -> request.getRequester().getId() != userId)
                                                    .collect(Collectors.toList());
        if (requests.isEmpty()) {
            throw new NotFoundException(String.format("Request not found with reqId=%s, eventId=%s", reqId, eventId));
        }
        Request request = requests.get(0);
        request.setStatus(Status.REJECTED);
        requestsRepository.save(request);
        log.info("Reject request users userId={}, eventId={}, reqId={}",userId, eventId, reqId);
        return RequestsMapper.toParticipationRequestDto(request);
    }

    private Event getModifiedEvent(Event eventDb, UpdateEventRequest eventRequest) {
        LocalDateTime eventDate = LocalDateTime.parse(eventRequest.getEventDate(), DATE_TIME_FORMATTER);
        if (!eventRequest.getAnnotation().isBlank() && eventRequest.getAnnotation() != null) {
            eventDb.setAnnotation(eventRequest.getAnnotation());
            log.info("Update annotation event new annotation={}", eventDb.getAnnotation());
        }
        if (eventRequest.getCategory() != null) {
            eventDb.setCategory(categoriesRepository.findById(eventRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException(
                            String.format("Category with id= %s not found", eventRequest.getCategory()))));
            log.info("Update category event, new category catIa={}", eventDb.getCategory().getId());
        }
        if (!eventRequest.getDescription().isBlank() && eventRequest.getDescription() != null) {
            eventDb.setDescription(eventRequest.getDescription());
            log.info("Update description event, new annotation={}", eventDb.getDescription());
        }
        if (eventRequest.getEventDate() != null) {
            eventDb.setEventDate(eventDate);
            log.info("Update event date new dataTime={}", eventDb.getEventDate());
        }
        if (eventRequest.getPaid() != null) {
            eventDb.setPaid(eventRequest.getPaid());
            log.info("Update paid event, new paid={}", eventDb.getPaid());
        }
        if (eventRequest.getParticipantLimit() != null) {
            eventDb.setParticipantLimit(eventRequest.getParticipantLimit());
            log.info("Update participant limit event, new limit={}", eventDb.getParticipantLimit());
        }
        if (!eventRequest.getTitle().isBlank() && eventRequest.getTitle() != null) {
            eventDb.setTitle(eventRequest.getTitle());
            log.info("Update title event, new title={}", eventDb.getTitle());
        }

        log.info("Event data update completed");
        return eventDb;
    }

    private Event get(Long eventId, Long userId) {
        return repository.findEventByIdAndInitiatorId(eventId, userId)
        .orElseThrow(() ->
                   new NotFoundException(String.format("Event not found with userId=%s, eventId=%s", userId, eventId)));
    }

    private EventFullDto save(Event event) {
        repository.save(event);
        return EventMapper.toEventFullDto(event, eventClient.get(List.of(event)));
    }
}
