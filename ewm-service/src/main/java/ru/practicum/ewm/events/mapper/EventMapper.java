package ru.practicum.ewm.events.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.categories.mapper.CategoriesMapper;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.events.dto.AdminUpdateEventRequest;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.model.State;
import ru.practicum.ewm.requests.model.Status;
import ru.practicum.ewm.users.mapper.UsersMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.Location;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class EventMapper {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Event toEvent(NewEventDto newEventDto) {
        Event event = new Event();
        event.setAnnotation(newEventDto.getAnnotation());
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(LocalDateTime.parse(newEventDto.getEventDate(), DATE_TIME_FORMATTER));
        event.setCreatedOn(LocalDateTime.now());
        event.setPaid(newEventDto.isPaid());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.isRequestModeration());
        event.setTitle(newEventDto.getTitle());
        event.setLat(newEventDto.getLocation().getLat());
        event.setLon(newEventDto.getLocation().getLon());
        event.setState(State.PENDING);
        return event;
    }

    public static Event getEvent(Event event, AdminUpdateEventRequest adminUpdateEvent, Category category) {
        event.setAnnotation(adminUpdateEvent.getAnnotation());
        event.setCategory(category);
        event.setDescription(adminUpdateEvent.getDescription());
        event.setEventDate(LocalDateTime.parse(adminUpdateEvent.getEventDate(), DATE_TIME_FORMATTER));
        event.setPaid(adminUpdateEvent.isPaid());
        event.setParticipantLimit(adminUpdateEvent.getParticipantLimit());
        event.setRequestModeration(adminUpdateEvent.isRequestModeration());
        event.setTitle(adminUpdateEvent.getTitle());
        return event;
    }

    public static EventFullDto toEventFullDto(Event event,  Map<Long, Long> views) {
        return new EventFullDto(event.getId(),
                event.getAnnotation(),
                CategoriesMapper.toCategoryDto(event.getCategory()),
                getConfirmedRequests(event),
                event.getCreatedOn().format(DATE_TIME_FORMATTER),
                event.getDescription(),
                event.getEventDate().format(DATE_TIME_FORMATTER),
                UsersMapper.toUserShortDto(event.getInitiator()),
                new Location(event.getLat(), event.getLon()),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn() != null ? event.getPublishedOn().format(DATE_TIME_FORMATTER) : null,
                event.isRequestModeration(),
                event.getState(),
                event.getTitle(),
                getViews(event.getId(), views));
    }

    public static EventShortDto toEventShortDto(Event event,  Map<Long, Long> views) {
        return new EventShortDto(event.getId(),
                event.getAnnotation(),
                CategoriesMapper.toCategoryDto(event.getCategory()),
                getConfirmedRequests(event),
                event.getEventDate().format(DATE_TIME_FORMATTER),
                UsersMapper.toUserShortDto(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                getViews(event.getId(), views));
    }

    private static long getConfirmedRequests(Event event) {
        if (event.getRequests() == null) {
            return 0L;
        }
        return event.getRequests().stream().filter(e -> e.getStatus() == Status.CONFIRMED).count();
    }

    private static long getViews(Long eventId, Map<Long, Long> views) {
        Long viewsEvent = views.get(eventId);
        if (viewsEvent == null) {
            return 0L;
        }
        return viewsEvent;
    }
}