package ru.practicum.ewm.users.service;

import ru.practicum.ewm.exeption.NotFoundException;
import ru.practicum.ewm.users.mapper.UsersMapper;
import ru.practicum.ewm.users.model.User;
import ru.practicum.ewm.users.repository.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.users.dto.NewUserRequest;
import ru.practicum.ewm.users.dto.UserDto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminUsersServiceImpl implements AdminUsersService {

    private final UsersRepository repository;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public AdminUsersServiceImpl(UsersRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<UserDto> getAllUsers(String ids, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Long> idList = Arrays.stream(ids.split(",")).map(Long::valueOf).collect(Collectors.toList());
        logger.info("Get users with ids = " + ids);
        return repository.findAllById(idList, pageable).stream()
                                                       .map(UsersMapper::toUserDto)
                                                       .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(NewUserRequest newUser) {
        User user = repository.save(UsersMapper.toUser(newUser));
        logger.info("Save new user={}", user);
        return UsersMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        repository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id= %s was not found", userId)));
        repository.deleteById(userId);
        logger.info("Delete user with userId={}", userId);
    }
}
