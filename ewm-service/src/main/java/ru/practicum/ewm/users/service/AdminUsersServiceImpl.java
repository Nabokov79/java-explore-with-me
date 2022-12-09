package ru.practicum.ewm.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.exeption.NotFoundException;
import ru.practicum.ewm.users.mapper.UsersMapper;
import ru.practicum.ewm.users.model.User;
import ru.practicum.ewm.users.repository.FriendshipRepository;
import ru.practicum.ewm.users.repository.SubscriptionRepository;
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
    private final FriendshipRepository friendsRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    public List<UserDto> getAll(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        log.info("Get users with ids = " + ids);
        if (!ids.isEmpty()) {
            return UsersMapper.toListDto(repository.findAllById(ids));
        } else {
            return UsersMapper.toListDto(repository.findAll(pageable).getContent());
        }
    }

    @Override
    public UserDto create(NewUserRequest newUser, Boolean subscription) {
        User user = repository.save(UsersMapper.toUser(newUser, subscription));
        log.info("Save new user={}", user);
        return UsersMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long userId, Boolean subscription) {
        User user = getUser(userId);
        user.setSubscription(subscription);
        log.info("Update user with userId={}, subscription={}", userId, subscription);
        return UsersMapper.toUserDto(repository.save(user));
    }

    @Override
    public void delete(Long userId) {
        getUser(userId);
        friendsRepository.deleteAllByUserId(userId);
        subscriptionRepository.deleteAllByUserId(userId);
        repository.deleteById(userId);
        log.info("Delete user with userId={}", userId);
    }

    private User getUser(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User with id= %s was not found", id)));
    }
}
