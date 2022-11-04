package ru.practicum.ewm.events.service;

import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.dto.UpdateEventRequest;
import ru.practicum.ewm.requests.dto.ParticipationRequestDto;
import java.util.List;

public interface PrivateEventsService {

    List<EventFullDto> getEventByUserId(Long userId,int from, int size);

    EventFullDto changeEventByUserId(Long userId, UpdateEventRequest eventRequest);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEventInfoCurrentUser(Long userId,Long eventId);

    EventFullDto cancelEvent(Long userId, Long eventId);

    List<ParticipationRequestDto> getInfoRequestEventsCurrentUser(Long userId, Long eventId);

    ParticipationRequestDto confirmRequestUsers(Long userId, Long eventId, Long reqId);

    ParticipationRequestDto rejectRequestUsers(Long userId, Long eventId, Long reqId);
}