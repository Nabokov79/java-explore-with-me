package ru.practicum.ewm.events.dto;

import ru.practicum.ewm.events.model.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Setter
@Getter
@AllArgsConstructor
public class NewEventDto {

    @Size(min = 20, message = "min length is 20 characters")
    @Size(max = 2000, message = "max length is 2000 characters")
    @NotBlank(message = "should not be blank")
    private String annotation;
    @NotNull(message = "should not be blank")
    private Long category;
    @Size(min = 20, message = "min length is 20 characters")
    @Size(max = 7000, message = "max length is 7000 characters")
    @NotBlank(message = "date should not be blank")
    private String description;
    @NotBlank(message = "should not be blank")
    private String eventDate;
    @NotNull(message = "should not be blank")
    private Location location;
    private boolean paid;
    @PositiveOrZero(message = "not positive")
    private int participantLimit;
    private boolean requestModeration;
    @Size(min = 3, message = "min length is 3 characters")
    @Size(max = 120, message = "max length is 120 characters")
    @NotBlank(message = "should not be blank")
    private String title;
}