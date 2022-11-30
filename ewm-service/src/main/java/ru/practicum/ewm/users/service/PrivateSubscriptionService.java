package ru.practicum.ewm.users.service;


import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.users.dto.UserDto;
import java.util.List;

public interface PrivateSubscriptionService {

    List<UserDto> subscribe(Long userId, Long subscriberId);

    void unsubscribe(Long userId, Long subscriberId);

    List<UserDto> get(Long userId);

    List<EventFullDto> getEvents(Long userId, Long subscriberId, int from, int size);
}
