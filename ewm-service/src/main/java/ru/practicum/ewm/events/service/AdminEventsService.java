package ru.practicum.ewm.events.service;

import ru.practicum.ewm.events.dto.AdminUpdateEventRequest;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.paramRequest.ParamAdminRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface AdminEventsService {

    List<EventFullDto> search(ParamAdminRequest param, HttpServletRequest request, int from, int size);

    EventFullDto edit(Long eventId, AdminUpdateEventRequest adminUpdateEvent, HttpServletRequest request);

    EventFullDto publish(Long eventId, HttpServletRequest request);

    EventFullDto reject(Long eventId, HttpServletRequest request);
}
