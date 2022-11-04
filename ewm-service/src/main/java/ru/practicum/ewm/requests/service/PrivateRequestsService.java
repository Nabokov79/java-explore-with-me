package ru.practicum.ewm.requests.service;

import ru.practicum.ewm.requests.dto.ParticipationRequestDto;

import java.util.List;

public interface PrivateRequestsService {

    List<ParticipationRequestDto> getRequestByUserId(Long userId);

    ParticipationRequestDto addRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);
}
