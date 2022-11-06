package ru.practicum.ewm.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.exeption.NotFoundException;
import ru.practicum.ewm.users.mapper.UsersMapper;
import ru.practicum.ewm.users.model.User;
import ru.practicum.ewm.users.repository.UsersRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.users.dto.NewUserRequest;
import ru.practicum.ewm.users.dto.UserDto;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminUsersServiceImpl implements AdminUsersService {

    private final UsersRepository repository;

    @Override
    public List<UserDto> getAll(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        log.info("Get users with ids = " + ids);
        return UsersMapper.toListDto(repository.findAllById(ids, pageable));
    }

    @Override
    public UserDto create(NewUserRequest newUser) {
        User user = repository.save(UsersMapper.toUser(newUser));
        log.info("Save new user={}", user);
        return UsersMapper.toUserDto(user);
    }

    @Override
    public void delete(Long userId) {
        repository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id= %s was not found", userId)));
        repository.deleteById(userId);
        log.info("Delete user with userId={}", userId);
    }
}
