package ru.practicum.ewm.events.dto;

import ru.practicum.ewm.common.Create;
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

    @Size(groups = {Create.class}, min = 20, message = "min annotation length is 20 characters")
    @Size(groups = {Create.class}, max = 2000, message = "max annotation length is 2000 characters")
    @NotBlank(groups = {Create.class}, message = "annotation date should not be blank")
    private String annotation;
    @NotNull(groups = {Create.class}, message = "category should not be blank")
    private Long category;
    @Size(groups = {Create.class}, min = 20, message = "min description length is 20 characters")
    @Size(groups = {Create.class}, max = 7000, message = "max description length is 7000 characters")
    @NotBlank(groups = {Create.class}, message = "description date should not be blank")
    private String description;
    @NotBlank(groups = {Create.class}, message = "event date should not be blank")
    private String eventDate;
    @NotNull(groups = {Create.class}, message = "location should not be blank")
    private Location location;
    private boolean paid;
    @PositiveOrZero(groups = {Create.class}, message = "participant limit not positive")
    private int participantLimit;
    private boolean requestModeration;
    @Size(groups = {Create.class}, min = 3, message = "min title length is 3 characters")
    @Size(groups = {Create.class},max = 120, message = "max title length is 120 characters")
    @NotBlank(groups = {Create.class}, message = "title date should not be blank")
    private String title;
}