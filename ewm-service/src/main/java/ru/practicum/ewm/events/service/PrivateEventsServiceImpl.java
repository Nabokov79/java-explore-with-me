package ru.practicum.ewm.events.service;

import ru.practicum.ewm.categories.repository.CategoriesRepository;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.dto.UpdateEventRequest;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.State;
import ru.practicum.ewm.paramRequest.Param;
import ru.practicum.ewm.exeption.BadRequestException;
import ru.practicum.ewm.exeption.NotFoundException;
import ru.practicum.ewm.requests.dto.ParticipationRequestDto;
import ru.practicum.ewm.events.repository.EventsRepository;
import ru.practicum.ewm.requests.mapper.RequestsMapper;
import ru.practicum.ewm.requests.model.Request;
import ru.practicum.ewm.requests.model.Status;
import ru.practicum.ewm.requests.repository.RequestsRepository;
import ru.practicum.ewm.users.repository.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PrivateEventsServiceImpl implements PrivateEventsService {

    private final EventsRepository repository;
    private final UsersRepository usersRepository;
    private final CategoriesRepository categoriesRepository;
    private final RequestsRepository requestsRepository;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public PrivateEventsServiceImpl(EventsRepository repository,
                                    UsersRepository usersRepository,
                                    CategoriesRepository categoriesRepository,
                                    RequestsRepository requestsRepository) {
        this.repository = repository;
        this.usersRepository = usersRepository;
        this.categoriesRepository = categoriesRepository;
        this.requestsRepository = requestsRepository;
    }

    @Override
    public List<EventFullDto> getEventByUserId(Long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> eventList = repository.findAllByInitiatorId(userId, pageable);
        if (eventList.isEmpty()) {
            throw new NotFoundException(String.format("Events not found by userId=%s", userId));
        }
        logger.info("Get event by userId={} with parameters from={}, size={}", userId, from, size);
        return eventList.stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto changeEventByUserId(Long userId, UpdateEventRequest eventRequest) {
        Event eventDb = repository.findEventByIdAndInitiatorId(eventRequest.getEventId(), userId);
        if (eventDb == null) {
            throw new NotFoundException(
                        String.format("Event not found with userId=%s, eventId=%s", userId, eventRequest.getEventId()));
        }
        if (LocalDateTime.parse(eventRequest.getEventDate(), Param.DATE_TIME_FORMATTER)
                                                                          .isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException(
                           String.format("Time of the event is set incorrectly eventDate=%s", eventDb.getEventDate()));
        }
        if (eventDb.getState().equals(State.PUBLISHED)) {
            throw new BadRequestException("Event is published, cannot be changed");
        }

        if (eventDb.getState().equals(State.CANCELED)) {
            eventDb.setPaid(true);
        }
        EventFullDto eventFullDto = EventMapper.toEventFullDto(getModifiedEvent(eventDb, eventRequest));
        eventFullDto.setConfirmedRequests(
                                      requestsRepository.countAllByEventIdAndStatus(eventDb.getId(), Status.CONFIRMED));
        repository.save(eventDb);
        logger.info("Update event by initiator userId={} ", userId);
        return eventFullDto;
    }

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
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
        repository.save(event);
        logger.info("Create event eventId={}", event.getId());
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto getEventInfoCurrentUser(Long userId, Long eventId) {
        Event event = repository.findEventByIdAndInitiatorId(eventId, userId);
        if (event == null) {
            throw new NotFoundException(String.format("Event not found with userId=%s, eventId=%s", userId, eventId));
        }
        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        eventFullDto.setConfirmedRequests(requestsRepository.countAllByEventIdAndStatus(eventId, Status.CONFIRMED));
        logger.info("Get event info current user userId={}", userId);
        return eventFullDto;
    }

    @Override
    public EventFullDto cancelEvent(Long userId, Long eventId) {
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event not found with eventId=%s", eventId)));
        if (event.getInitiator().getId() != userId) {
            throw new BadRequestException(String.format("User is not the initiator of the event? user=%s", userId));
        }
        if (event.getState().equals(State.PUBLISHED)) {
            throw new BadRequestException(String.format("Event moderation is required, state=%s", event.getState()));
        }
        event.setState(State.CANCELED);
        repository.save(event);
        logger.info("Event cancel eventId={}", eventId);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public List<ParticipationRequestDto> getInfoRequestEventsCurrentUser(Long userId, Long eventId) {
        List<Request> request = requestsRepository.findAllByEventId(eventId).stream()
                                                          .filter(request1 -> request1.getRequester().getId() != userId)
                                                          .collect(Collectors.toList());
        if (request.isEmpty()) {
            throw new NotFoundException(String.format("Request not found with userId=%s, eventId=%s", userId, eventId));
        }
        logger.info("Get info request events current user userId={}, eventId={}",userId, eventId);
        return request.stream().map(RequestsMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto confirmRequestUsers(Long userId, Long eventId, Long reqId) {
        Request requestDb = requestsRepository.findById(reqId)
                .orElseThrow(() -> new NotFoundException(String.format("Request with id= %s was not found", userId)));
        Event event = repository.findEventByIdAndInitiatorId(eventId, userId);
        Set<Request> requestList = requestsRepository.findAllByEventId(eventId);
        long countDb = requestsRepository.countAllByEventId(eventId);
        long count = Long.valueOf(event.getParticipantLimit());
        if (countDb < count) {
            requestDb.setStatus(Status.CONFIRMED);
            requestsRepository.save(requestDb);
        }

        if (countDb == count) {
            requestList.forEach(request -> request.setCreated(event.getEventDate()));
            requestsRepository.saveAll(requestList);
        }
        requestsRepository.save(requestDb);
        logger.info("Confirm request users userId={}, eventId={}, reqId={}",userId, eventId, reqId);
        return RequestsMapper.toParticipationRequestDto(requestDb);
    }

    @Override
    public ParticipationRequestDto rejectRequestUsers(Long userId, Long eventId, Long reqId) {
        List<Request> requestList = requestsRepository.findByIdAndEventId(reqId, eventId).stream()
                .filter(request1 -> request1.getRequester().getId() != userId)
                .collect(Collectors.toList());
        if (requestList.isEmpty()) {
            throw new NotFoundException(String.format("Request not found with reqId=%s, eventId=%s", reqId, eventId));
        }
        Request request = requestList.get(0);
        request.setStatus(Status.REJECTED);
        requestsRepository.save(request);
        logger.info("Reject request users userId={}, eventId={}, reqId={}",userId, eventId, reqId);
        return RequestsMapper.toParticipationRequestDto(request);
    }

    private Event getModifiedEvent(Event eventDb, UpdateEventRequest eventRequest) {
        if (!eventDb.getAnnotation().equals(eventRequest.getAnnotation().toLowerCase())) {
            eventDb.setAnnotation(eventRequest.getAnnotation());
            logger.info("Update annotation event new annotation={}", eventDb.getAnnotation());
        }

        if (eventDb.getCategory().getId() != eventRequest.getCategory()) {
            eventDb.setCategory(categoriesRepository.findById(eventRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException(
                            String.format("Category with id= %s not found", eventRequest.getCategory()))));
            logger.info("Update category event, new category catIa={}", eventDb.getCategory().getId());
        }

        if (!eventDb.getDescription().equals(eventRequest.getDescription().toLowerCase())) {
            eventDb.setDescription(eventRequest.getDescription());
            logger.info("Update description event, new annotation={}", eventDb.getDescription());
        }

        if (!eventDb.getEventDate().isEqual(LocalDateTime.parse(eventRequest.getEventDate(),
                                                                                         Param.DATE_TIME_FORMATTER))) {
            eventDb.setEventDate(LocalDateTime.parse(eventRequest.getEventDate(), Param.DATE_TIME_FORMATTER));
            logger.info("Update event date new dataTime={}", eventDb.getEventDate());
        }

        if (!eventDb.getPaid().equals(eventRequest.getPaid())) {
            eventDb.setPaid(eventRequest.getPaid());
            logger.info("Update paid event, new paid={}", eventDb.getPaid());
        }

        if (eventDb.getParticipantLimit() != eventRequest.getParticipantLimit()) {
            eventDb.setParticipantLimit(eventRequest.getParticipantLimit());
            logger.info("Update participant limit event, new limit={}", eventDb.getParticipantLimit());
        }

        if (!eventDb.getTitle().equals(eventRequest.getTitle().toLowerCase())) {
            eventDb.setTitle(eventRequest.getTitle());
            logger.info("Update title event, new title={}", eventDb.getTitle());
        }

        logger.info("Event data update completed");
        return eventDb;
    }
}
