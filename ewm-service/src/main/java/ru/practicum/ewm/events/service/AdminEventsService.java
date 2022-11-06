package ru.practicum.ewm.events.service;

import ru.practicum.ewm.events.dto.AdminUpdateEventRequest;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.paramRequest.ParamAdminRequest;

import java.util.List;

public interface AdminEventsService {

    List<EventFullDto> search(ParamAdminRequest param, int from, int size);

    EventFullDto edit(Long eventId, AdminUpdateEventRequest adminUpdateEvent);

    EventFullDto publish(Long eventId);

    EventFullDto reject(Long eventId);
}
