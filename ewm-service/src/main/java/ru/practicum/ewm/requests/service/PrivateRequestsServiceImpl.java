package ru.practicum.ewm.requests.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.stereotype.Service;
import ru.practicum.ewm.requests.dto.ParticipationRequestDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrivateRequestsServiceImpl implements PrivateRequestsService {

    private final RequestsRepository repository;
    private final EventsRepository eventsRepository;
    private final UsersRepository usersRepository;

    @Override
    public List<ParticipationRequestDto> getByUserId(Long userId) {
        List<ParticipationRequestDto> requests = RequestsMapper.toListDto(
                                                              new ArrayList<>(repository.findAllByRequesterId(userId)));
        log.info("Get list request by id={} ", userId);
        return requests;
    }

    @Override
    public ParticipationRequestDto add(Long userId, Long eventId) {
        log.info("SAVE REQUEST event with userId={}, eventId={}", userId, eventId);
        Event event = eventsRepository.findById(eventId)
                    .orElseThrow(() -> new NotFoundException(String.format("Event with id= %s was not found", userId)));
        Optional<Request> requestDb = repository.findByRequesterIdAndEventId(userId, eventId);
        if (requestDb.isPresent()) {
            log.info("Save request isPresent event with userId={}, eventId={}", userId, eventId);
        return RequestsMapper.toParticipationRequestDto(requestDb.get());
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
        if (!event.isRequestModeration()) {
            request.setStatus(Status.CONFIRMED);
        }
        log.info("Save request with userId={}, eventId={}", userId, eventId);
        ParticipationRequestDto request1 =  RequestsMapper.toParticipationRequestDto(repository.save(request));
        log.info("Save request with Id={}, eventId={}, userId={}", request1.getId(), request1.getEvent(), request1.getRequester());
        return request1;
    }

    @Override
    public ParticipationRequestDto cancel(Long userId, Long requestId) {
        Request request = repository.findByIdAndRequesterId(requestId, userId);
        if (request == null) {
            throw new NotFoundException(
                    String.format("Request by parameters userId= %s, requestId= %s not found", userId, requestId)
            );
        }
        request.setStatus(Status.CANCELED);
        repository.save(request);
        log.info("Get request with userId={}, requestId={}", userId, requestId);
        return RequestsMapper.toParticipationRequestDto(request);
    }
}
