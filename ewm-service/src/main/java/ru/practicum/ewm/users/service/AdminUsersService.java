package ru.practicum.ewm.users.service;

import ru.practicum.ewm.users.dto.NewUserRequest;
import ru.practicum.ewm.users.dto.UserDto;

import java.util.List;

public interface AdminUsersService {

    List<UserDto> getAllUsers(String ids, int from, int size);

    UserDto createUser(NewUserRequest newUser);

   void deleteUser(Long userId);
}
