package ru.practicum.ewm.events.service;

import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.paramRequest.ParamUserRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface PublicEventsService {

    List<EventShortDto> getAll(ParamUserRequest paramUserRequest, int from, int size, HttpServletRequest request);

    EventFullDto getFullInfo(Long id, HttpServletRequest request);
}
