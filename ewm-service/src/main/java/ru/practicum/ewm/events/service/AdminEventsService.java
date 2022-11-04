package ru.practicum.ewm.events.service;

import ru.practicum.ewm.events.dto.AdminUpdateEventRequest;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.paramRequest.ParamAdminRequest;

import java.util.List;

public interface AdminEventsService {

    List<EventFullDto> searchEvents(ParamAdminRequest param, int from, int size);

    EventFullDto editEvent(Long eventId, AdminUpdateEventRequest adminUpdateEvent);

    EventFullDto publishEvent(Long eventId);

    EventFullDto rejectEvent(Long eventId);
}
