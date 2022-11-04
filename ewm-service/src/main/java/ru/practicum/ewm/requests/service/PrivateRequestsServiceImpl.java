package ru.practicum.ewm.requests.service;

import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.State;
import ru.practicum.ewm.events.repository.EventsRepository;
import ru.practicum.ewm.exeption.BadRequestException;
import ru.practicum.ewm.exeption.NotFoundException;
import ru.practicum.ewm.requests.mapper.RequestsMapper;
import ru.practicum.ewm.requests.model.Request;
import ru.practicum.ewm.requests.model.Status;
import ru.practicum.ewm.requests.repository.RequestsRepository;
import ru.practicum.ewm.users.repository.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.requests.dto.ParticipationRequestDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PrivateRequestsServiceImpl implements PrivateRequestsService {

    private final RequestsRepository repository;
    private final EventsRepository eventsRepository;
    private final UsersRepository usersRepository;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public PrivateRequestsServiceImpl(RequestsRepository repository,
                                      EventsRepository eventsRepository,
                                      UsersRepository usersRepository) {
        this.repository = repository;
        this.eventsRepository = eventsRepository;
        this.usersRepository = usersRepository;
    }

    @Override
    public List<ParticipationRequestDto> getRequestByUserId(Long userId) {
        List<ParticipationRequestDto> requestDtoList = repository.findAllByRequesterId(userId).stream()
                                                                         .map(RequestsMapper::toParticipationRequestDto)
                                                                         .collect(Collectors.toList());
        logger.info("Get list request by id={} ", userId);
        return requestDtoList;
    }

    @Override
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        Event event = eventsRepository.findById(eventId)
                    .orElseThrow(() -> new NotFoundException(String.format("Event with id= %s was not found", userId)));
        if (repository.findByRequesterIdAndEventId(userId, eventId).isPresent()) {
            throw new BadRequestException(
                    String.format("Bad request request with parameters userId= %s,eventId= %s found", userId, eventId));
        }

        if (event.getInitiator().getId() == userId) {
            throw new BadRequestException(
                          String.format("Bad request user is initiator event userId= %s,eventId= %s", userId, eventId));
        }

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new BadRequestException(
                    String.format("Bad request with parameters userId= %s,eventId= %s< state=%s", userId, eventId,
                                                                                                     event.getState()));
        }

        if (Objects.equals(Long.valueOf(event.getParticipantLimit()), repository.countAllByEventIdAndStatus(eventId,
                                                                                                   Status.CONFIRMED))) {
            throw new BadRequestException(
                    String.format("Bad request  participant limit with parameters userId= %s,eventId= %s, " +
                                                   "participantLimit=%s", userId, eventId,event.getParticipantLimit()));
        }
        Request request = RequestsMapper.toRequest();
        request.setEvent(event);
        request.setRequester(usersRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id= %s was not found", userId))));
        if (!event.getRequestModeration()) {
            request.setStatus(Status.CONFIRMED);
        }
        repository.save(request);
        logger.info("Save request with userId={}, eventId={}", userId, eventId);
        return RequestsMapper.toParticipationRequestDto(request);
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        List<Request> requestList = new ArrayList<>(repository.findByIdAndRequesterId(requestId, userId));
        if (requestList.isEmpty()) {
            throw new NotFoundException(
                    String.format("Request by parameters userId= %s, requestId= %s not found", userId, requestId)
            );
        }
        Request request = requestList.get(0);
        request.setStatus(Status.CANCELED);
        repository.save(request);
        logger.info("Get request with userId={}, requestId={}", userId, requestId);
        return RequestsMapper.toParticipationRequestDto(request);
    }
}
