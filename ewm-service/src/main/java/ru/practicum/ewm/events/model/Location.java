package ru.practicum.ewm.events.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Digits;

@Setter
@Getter
@AllArgsConstructor
public class Location {

    @Digits(integer = 2, fraction = 6)
    @Range(min = -90, max = 90)
    private Float lat;
    @Digits(integer = 3, fraction = 6)
    @Range(min = -180, max = 180)
    private Float lon;
}
