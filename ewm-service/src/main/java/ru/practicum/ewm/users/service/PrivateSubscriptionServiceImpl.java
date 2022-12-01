package ru.practicum.ewm.users.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.client.EventClient;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.QEvent;
import ru.practicum.ewm.events.repository.EventsRepository;
import ru.practicum.ewm.requests.model.QRequest;
import ru.practicum.ewm.requests.model.Status;
import ru.practicum.ewm.users.dto.UserDto;
import ru.practicum.ewm.users.mapper.UsersMapper;
import ru.practicum.ewm.users.model.Friendship;
import ru.practicum.ewm.users.model.StatusUser;
import ru.practicum.ewm.users.model.Subscription;
import ru.practicum.ewm.users.model.User;
import ru.practicum.ewm.users.repository.FriendshipRepository;
import ru.practicum.ewm.users.repository.SubscriptionRepository;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrivateSubscriptionServiceImpl implements PrivateSubscriptionService {

    private final SubscriptionRepository repository;
    private final EventsRepository eventsRepository;
    private final FriendshipRepository friendsRepository;
    private final EventClient eventClient;
    private final EntityManager entityManager;
    private final PrivateFriendsServiceImpl privateFriendsService;

    @Override
    public List<UserDto> subscribe(Long userId, Long subscriberId) {
        Map<Long, User> users = privateFriendsService.validateUsers(List.of(userId, subscriberId));
        if (users.get(userId).isSubscription()) {
            repository.save(UsersMapper.toSubscription(userId, subscriberId, true));
            return Stream.of(users.get(userId)).map(UsersMapper::toUserDto).collect(Collectors.toList());
        }
        log.info("Request subscription with parameters userId={}, requesterId={}", userId, subscriberId);
        return new ArrayList<>();
    }

    @Override
    public void unsubscribe(Long userId, Long subscriberId) {
        privateFriendsService.validateUsers(List.of(userId, subscriberId));
        Subscription subscription = repository.findByUserIdAndSubscriberId(userId, subscriberId);
        if (subscription.isStatus()) {
            subscription.setStatus(false);
            repository.save(subscription);
        }
        log.info("Request unsubscribe with parameters userId={}, requesterId={}", userId, subscriberId);
    }

    @Override
    public List<UserDto> get(Long userId) {
        log.info("Request get subscribers user userId={}", userId);
        return UsersMapper.toListDto(
                new ArrayList<>(privateFriendsService.validateUsers(List.of(userId)).get(userId).getSubscribers())
        );
    }

    @Override
    public List<EventFullDto> getEvents(Long userId, Long subscriberId, int from, int size) {
        privateFriendsService.validateUsers(List.of(userId, subscriberId));
        Friendship friendship = friendsRepository.findAllByUserIdAndFriendIdAndStatus(userId, subscriberId,
                                                                                              StatusUser.CONFIRMED);
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events;
        if (friendship != null) {
            log.info("Ged events for friend with parameters userId={}, subscriberId={}", userId, subscriberId);
            events = getPredicate(userId);
        } else {
            log.info("Ged events for subscriber with parameters userId={}, subscriberId={}", userId, subscriberId);
            events = eventsRepository.findAllByInitiatorId(userId, pageable);
        }
        Map<Long, Long> views = eventClient.get(events);
        return events.stream()
                     .map(event -> EventMapper.toEventFullDto(event, views))
                     .collect(Collectors.toList());
    }

    private  List<Event> getPredicate(Long userId) {
        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(QEvent.event.id.eq(QRequest.request.event.id));
        booleanBuilder.and(QRequest.request.requester.id.eq(userId));
        booleanBuilder.and(QRequest.request.status.eq(Status.CONFIRMED));
        return query.selectFrom(QEvent.event)
                    .leftJoin(QEvent.event.requests, QRequest.request).fetchJoin()
                    .where(booleanBuilder)
                    .fetch();
    }
}
