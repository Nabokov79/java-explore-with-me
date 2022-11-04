package ru.practicum.ewm.events.dto;

import ru.practicum.ewm.events.model.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter
@Getter
@AllArgsConstructor
public class NewEventDto {

    @Size(min = 20, message = "min annotation length is 20 characters")
    @Size(max = 2000, message = "max annotation length is 2000 characters")
    private String annotation;
    @NotBlank(message = "category should not be blank")
    private Long category;
    @Size(min = 20, message = "min description length is 20 characters")
    @Size(max = 7000, message = "max description length is 7000 characters")
    private String description;
    @NotBlank(message = "event date should not be blank")
    private String eventDate;
    @NotBlank(message = "location should not be blank")
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    @Size(min = 3, message = "min title length is 3 characters")
    @Size(max = 120, message = "max title length is 120 characters")
    private String title;
}