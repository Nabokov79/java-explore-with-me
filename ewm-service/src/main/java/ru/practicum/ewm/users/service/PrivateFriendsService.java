package ru.practicum.ewm.users.service;

import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.ewm.users.dto.UserDto;
import ru.practicum.ewm.users.model.Friendship;
import java.util.List;

public interface PrivateFriendsService {

    void request(Long userId, Long requesterId);

    List<Friendship> getRequests(@PathVariable Long userId);

    void confirm(Long userId, Long requesterId);

    void reject(Long userId, Long requesterId);

    List<UserDto> get(Long userId);

    void delete(Long userId, Long friendId);
}