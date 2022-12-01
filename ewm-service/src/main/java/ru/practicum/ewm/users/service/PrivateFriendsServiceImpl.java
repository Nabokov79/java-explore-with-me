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
    private final FriendshipRepository friendshipRepository;

    @Override
    public void request(Long userId, Long requesterId) {
        log.info("Request friendship with parameters userId={}, requesterId={}", userId, requesterId);
        Friendship friendship = getFriendship(userId, requesterId);
        if (friendship == null || friendship.getStatus().equals(StatusUser.REJECTED)) {
            friendshipRepository.save(UsersMapper.toFriendship(userId, requesterId, StatusUser.PENDING));
        } else {
            throw new BadRequestException("Your request is awaiting confirmation or has been confirmed");
        }
    }

    @Override
    public List<Friendship> getFriendshipRequests(Long userId) {
        log.info("Request  get friends for user with userId={}", userId);
        return new ArrayList<>(friendshipRepository.findAllByUserIdAndStatus(userId, StatusUser.PENDING));
    }

    @Override
    public void confirm(Long userId, Long requesterId) {
        validateUsers(List.of(userId, requesterId));
        Friendship user = getFriendship(userId, requesterId);
        Friendship friend = getFriendship(requesterId, userId);
        if (user != null) {
            user.setStatus(StatusUser.CONFIRMED);
        } else {
            throw new BadRequestException(
                 String.format("Request with parameters userId=%s and requesterId=%S not found.", userId, requesterId)
            );
        }
        if (friend == null) {
            friend = UsersMapper.toFriendship(requesterId, userId, StatusUser.CONFIRMED);
            friendshipRepository.saveAll(List.of(user, friend));
        }
        log.info("Request confirm friendship with parameters userId={}, requesterId={}", userId, requesterId);
    }

    @Override
    public void reject(Long userId, Long requesterId) {
        validateUsers(List.of(userId, requesterId));
        Friendship user = getFriendship(userId, requesterId);
        user.setStatus(StatusUser.REJECTED);
        friendshipRepository.save(user);
        log.info("Request reject friendship with parameters userId={}, requesterId={}", userId, requesterId);
    }

    @Override
    public List<UserDto> getFriends(Long userId) {
        log.info("Request get friends users with userId={}", userId);
        return UsersMapper.toListDto(new ArrayList<>(validateUsers(List.of(userId)).get(userId).getFriends()));
    }

    @Override
    public void delete(Long userId, Long friendId) {
        log.info("Request delete friendship with parameters userId={}, friendId={}", userId, friendId);
        friendshipRepository.deleteAllByUserIdAndFriendId(userId, friendId);
    }

    private Friendship getFriendship(Long userId, Long friendId) {
        log.info("Validated ids userId={}, friendId={}", userId, friendId);
        return friendshipRepository.findByUserIdAndFriendId(userId, friendId);
    }

    protected Map<Long, User> validateUsers(List<Long> ids) {
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
