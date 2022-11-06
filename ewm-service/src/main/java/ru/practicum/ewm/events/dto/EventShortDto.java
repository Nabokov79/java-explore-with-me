package ru.practicum.ewm.events.dto;

import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.users.dto.UserShortDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class EventShortDto {

    private Long id;
    private String annotation;
    private CategoryDto category;
    private Integer confirmedRequests;
    private String eventDate;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Long views;
}
