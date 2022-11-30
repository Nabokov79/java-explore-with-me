package ru.practicum.ewm.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exeption.BadRequestException;
import ru.practicum.ewm.exeption.NotFoundException;
import ru.practicum.ewm.users.mapper.UsersMapper;
import ru.practicum.ewm.users.model.*;
import ru.practicum.ewm.users.dto.UserDto;
import ru.practicum.ewm.users.repository.FriendshipRepository;
import ru.practicum.ewm.users.repository.UsersRepository;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrivateFriendsServiceImpl implements PrivateFriendsService {

    private final UsersRepository usersRepository;
    private final FriendshipRepository friendsRepository;

    @Override
    public void request(Long userId, Long requesterId) {
        log.info("Request friendship with parameters userId={}, requesterId={}", userId, requesterId);
        Friendship friendship = validateFriendship(userId, requesterId);
        if (friendship == null || friendship.getStatus().equals(StatusUser.REJECTED)) {
            friendsRepository.save(UsersMapper.toFriends(userId, requesterId, StatusUser.PENDING));
        } else {
            throw new BadRequestException("Your request is awaiting confirmation or has been confirmed");
        }
    }

    @Override
    public List<Friendship> getRequests(Long userId) {
        log.info("Request  get friends for user with userId={}", userId);
        return new ArrayList<>(friendsRepository.findAllByUserIdAndStatus(userId, StatusUser.PENDING));
    }

    @Override
    public void confirm(Long userId, Long requesterId) {
        getUsers(List.of(userId, requesterId));
        Friendship user = validateFriendship(userId, requesterId);
        Friendship friend = validateFriendship(requesterId, userId);
        if (user != null) {
            user.setStatus(StatusUser.CONFIRMED);
        } else {
            throw new BadRequestException(
                 String.format("Request with parameters userId=%s and requesterId=%S not found.", userId, requesterId)
            );
        }
        if (friend == null) {
            friend = UsersMapper.toFriends(requesterId, userId, StatusUser.CONFIRMED);
            friendsRepository.saveAll(List.of(user, friend));
        }
        log.info("Request confirm friendship with parameters userId={}, requesterId={}", userId, requesterId);
    }

    @Override
    public void reject(Long userId, Long requesterId) {
        getUsers(List.of(userId, requesterId));
        Friendship user = validateFriendship(userId, requesterId);
        user.setStatus(StatusUser.REJECTED);
        friendsRepository.save(user);
        log.info("Request reject friendship with parameters userId={}, requesterId={}", userId, requesterId);
    }

    @Override
    public List<UserDto> get(Long userId) {
        log.info("Request get friends users with userId={}", userId);
        return UsersMapper.toListDto(new ArrayList<>(getUsers(List.of(userId)).get(userId).getFriends()));
    }

    @Override
    public void delete(Long userId, Long friendId) {
        log.info("Request delete friendship with parameters userId={}, friendId={}", userId, friendId);
        friendsRepository.deleteAllByUserIdAndFriendId(userId, friendId);
    }

    private Friendship validateFriendship(Long userId, Long friendId) {
        log.info("Validated ids userId={}, friendId={}", userId, friendId);
        return friendsRepository.findByUserIdAndFriendId(userId, friendId);
    }

    protected Map<Long, User> getUsers(List<Long> ids) {
        log.info("Get users with ids={}", ids);
        Map<Long, User> users = usersRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        if (users.isEmpty()) {
            throw new NotFoundException(String.format("Users with ids=%s not found", ids));
        }
        if (users.size() != ids.size()) {
            List<Long> idsDb = new ArrayList<>();
            for (Long id : ids) {
                if (users.get(id) == null) {
                    idsDb.add(id);
                }
            }
            throw new NotFoundException(String.format("User with id= %s not found", idsDb));
        }
        return users;
    }
}
