package ru.practicum.ewm.events.mapper;

import ru.practicum.ewm.categories.mapper.CategoriesMapper;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.model.State;
import ru.practicum.ewm.paramRequest.Param;
import ru.practicum.ewm.users.mapper.UsersMapper;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.Location;

import java.time.LocalDateTime;

@Component
public class EventMapper {

    public static Event toEvent(NewEventDto newEventDto) {
        Event event = new Event();
        event.setAnnotation(newEventDto.getAnnotation());
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(LocalDateTime.parse(newEventDto.getEventDate(), Param.DATE_TIME_FORMATTER));
        event.setCreatedOn(LocalDateTime.now());
        event.setPaid(newEventDto.getPaid());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.getRequestModeration());
        event.setTitle(newEventDto.getTitle());
        event.setInitiator(null);
        event.setCategory(null);
        event.setLat(newEventDto.getLocation().getLat());
        event.setLon(newEventDto.getLocation().getLon());
        event.setState(State.PENDING);
        return event;
    }

    public static EventFullDto toEventFullDto(Event event) {
        return new EventFullDto(event.getId(),
                                event.getAnnotation(),
                                CategoriesMapper.toCategoryDto(event.getCategory()),
                                null,
                                event.getCreatedOn().format(Param.DATE_TIME_FORMATTER),
                                event.getDescription(),
                                event.getEventDate().format(Param.DATE_TIME_FORMATTER),
                                UsersMapper.toUserShortDto(event.getInitiator()),
                                new Location(event.getLat(), event.getLon()),
                                event.getPaid(),
                                event.getParticipantLimit(),
                                event.getPublishedOn() != null ? event.getPublishedOn()
                                                                              .format(Param.DATE_TIME_FORMATTER) : null,
                                event.getRequestModeration(),
                                event.getState(),
                                event.getTitle(),
                                null);
    }

    public static EventShortDto toEventShortDto(Event event) {
        return new EventShortDto(event.getId(),
                event.getAnnotation(),
                CategoriesMapper.toCategoryDto(event.getCategory()),
                null,
                event.getEventDate().format(Param.DATE_TIME_FORMATTER),
                UsersMapper.toUserShortDto(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                null);
    }
}