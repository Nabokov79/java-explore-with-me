package ru.practicum.ewm.users.service;

import ru.practicum.ewm.users.dto.NewUserRequest;
import ru.practicum.ewm.users.dto.UserDto;

import java.util.List;

public interface AdminUsersService {

    List<UserDto> getAll(List<Long> ids, int from, int size);

    UserDto create(NewUserRequest newUser);

   void delete(Long userId);
}
