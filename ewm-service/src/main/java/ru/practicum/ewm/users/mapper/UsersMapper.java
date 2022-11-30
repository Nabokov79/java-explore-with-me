package ru.practicum.ewm.users.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.users.dto.NewUserRequest;
import ru.practicum.ewm.users.dto.UserDto;
import ru.practicum.ewm.users.dto.UserShortDto;
import ru.practicum.ewm.users.model.*;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UsersMapper {

    public static User toUser(NewUserRequest userDto, Boolean subscription) {
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setSubscription(subscription);
        return user;
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail(), user.isSubscription());
    }

    public static UserShortDto toUserShortDto(User user) {
        return new UserShortDto(user.getId(), user.getName());
    }

    public static List<UserDto> toListDto(List<User> users) {
        return users.stream().map(UsersMapper::toUserDto).collect(Collectors.toList());
    }

    public static Friendship toFriends(Long userId, Long requesterId, StatusUser status) {
        Friendship friend = new Friendship();
        friend.setUserId(userId);
        friend.setFriendId(requesterId);
        friend.setStatus(status);
        return friend;
    }

    public static Subscription toSubscription(Long userId, Long subscriberId, boolean status) {
        Subscription subscription = new Subscription();
        subscription.setUserId(userId);
        subscription.setSubscriberId(subscriberId);
        subscription.setStatus(status);
        return subscription;
    }
}
